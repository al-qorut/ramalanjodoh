package smk.adzikro.ramalanjodoh.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.common.collect.ImmutableList
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import smk.adzikro.ramalanjodoh.ui.activities.BaseActivity
import java.lang.ref.WeakReference
import java.util.Collections.emptyList
import javax.inject.Inject

@ActivityScoped
class BillingManager @Inject constructor(
    @ActivityContext private val context: Context,
) {
    var productDetailsList = mutableListOf<ProductDetails>()

    private val scope = CoroutineScope(Dispatchers.Main)
    private var activityRef: WeakReference<BaseActivity>? = null

    fun initActivity(activity: BaseActivity) {
        this.activityRef = WeakReference(activity)
    }

    private fun getActivity(): BaseActivity? = activityRef?.get()

    private val listenerPurchase = PurchasesUpdatedListener { billingResult, purchases ->
        val activity = getActivity() // ?: return@PurchasesUpdatedListener
        if (activity == null) {
            context.mydebug("DEBUG_BILLING: Kebocoran! Activity sudah null di WeakReference!")
            return@PurchasesUpdatedListener
        }

        context.mydebug("DEBUG_BILLING: Response Code = ${billingResult.responseCode}")

        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                context.mydebug("DEBUG_BILLING: Status Pembelian = ${purchase.purchaseState} (1 = PURCHASED)")
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    handlePurchaseVerification(purchase)
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            context.mydebug("DEBUG_BILLING: Gagal/Cancel. Code: ${billingResult.responseCode}, Msg: ${billingResult.debugMessage}")
            activity.showErrorToast("Pembelian dibatalkan")
        } else {
            context.mydebug("DEBUG_BILLING: Gagal/Cancel. Code: ${billingResult.responseCode}, Msg: ${billingResult.debugMessage}")
            activity.showErrorToast("Gagal/Cancel melakukan pembelian: ${billingResult.debugMessage}")
        }
    }

    val billingClient by lazy {
        val activity = getActivity()
            ?: throw IllegalStateException("Activity belum diinisialisasi. Panggil initActivity() terlebih dahulu.")
        BillingClient.newBuilder(activity)
            .setListener(listenerPurchase)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts() // Mendukung transaksi tertunda untuk in-app purchase biasa
                    // .enablePrepaidPlans() // Buka komentar ini jika Anda juga menjual Langganan Prabayar (Prepaid Subscriptions)
                    .build()
            )
            .build()
    }

    private fun handlePurchaseVerification(purchase: Purchase) {
        when {
            purchase.products.contains("token_prediksi") -> verifyPurchase(purchase)
            purchase.products.contains("token_ramal") -> verifyPurchase(purchase)
        }
    }

    private fun verifyPurchase(purchase: Purchase) {
        context.mydebug("DEBUG_BILLING: Memulai proses update data untuk produk asesi...")
        updateTokenCount(purchase)
    }
    private fun eksekusiConsumePlayStore(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                context.mydebug("DEBUG_BILLING: Produk BERHASIL dikonsumsi di Play Store.")
            } else {
                context.mydebug("DEBUG_BILLING: Gagal consume di Play Store. Code: ${billingResult.responseCode}")
            }
        }
    }

    private fun updateTokenCount(purchase: Purchase) {
        val activity = getActivity() ?: return
        val tokenTambahan = purchase.quantity * 5
        activity.viewModel.addBeliToken(tokenTambahan.toLong(),
            onSuccess = { count ->
                eksekusiConsumePlayStore(purchase)
                context.toast("Sukses tambah token $tokenTambahan menjadi $count")
            },
            onFailure = { e ->
                context.showErrorToast("Gagal menyimpan $e")
                tampilkanDialogGagalBeli(purchase)
            }
        )
    }

    private fun tampilkanDialogGagalBeli(purchase: Purchase) {
        AlertDialog.Builder(context)
            .setTitle("Koneksi Gagal")
            .setMessage("Pembelian Anda berhasil di Play Store, namun gagal disimpan ke akun Anda. Ingin mencoba simpan kembali?")
            .setPositiveButton("Coba Lagi") { _, _ ->
                // Panggil ulang fungsi yang sama menggunakan data purchase yang ada
                updateTokenCount(purchase)
            }
            .setNegativeButton("Bantuan / Batal") { dialog, _ ->
                context.toast("Pembelian menggantung. Jika tidak dicoba lagi, uang Anda akan otomatis kembali dalam 3 hari.")
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    fun startBillingConnection() {
        if (billingClient.isReady) return

        billingClient.startConnection(object : BillingClientStateListener {
            override  fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    context.mydebug("Billing terhubung dengan sukses")
                    scope.launch {
                        showProducts()
                    }
                } else {
                    context.mydebug("Gagal setup billing: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                context.mydebug("Koneksi billing terputus")
            }
        })
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        val activity = getActivity() ?: return
        val productDetailsParamsList = ImmutableList.of(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    // Menggunakan suspend function untuk query produk
    suspend fun showProducts() = withContext(Dispatchers.IO) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("token_prediksi")
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("token_ramal")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList =
                    productDetailsResult.productDetailsList ?: emptyList()
                Log.e("", "Produk berhasil dimuat: ${productDetailsList.size}")
            } else {
                Log.e("", "Gagal memuat produk: ${billingResult.debugMessage}")
            }
        }
    }

    private fun executePurchaseFlow(productId: String) {
        val activity = getActivity() ?: return
        if (billingClient.isReady) {
            val product = productDetailsList.find { it.productId == productId }
            if (product != null) {
                launchPurchaseFlow(product)
            } else {
                context.toast("Produk tidak ditemukan, silakan muat ulang")
                scope.launch {showProducts()  }

            }
        } else {
            startBillingConnection()
        }
    }


    fun beliToken() {
        executePurchaseFlow("token_ramal")
    }

}

