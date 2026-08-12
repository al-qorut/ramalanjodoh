package smk.adzikro.ramalanjodoh.utils

import android.content.Context
import android.util.Log
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
import com.android.tools.r8.internal.db
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import smk.adzikro.ramalanjodoh.ui.activities.BaseActivity
import java.lang.ref.WeakReference
import java.util.Collections.emptyList
import com.google.common.collect.ImmutableList

class BillingManager(private val context: Context) {
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
        val activity = getActivity() ?: return
        context.mydebug("DEBUG_BILLING: Memulai proses update data untuk produk asesi...")

        // 1. Ambil multiplier/jalankan update Firestore terlebih dahulu
        db.getTokenMultiplier(
            onSuccess = { multiplier ->
                // Update data di Firestore Anda
                updateTokenCount(purchase, multiplier)

                // 2. Jika Firestore SUKSES di-update, baru konsumsi produknya di Google Play
                eksekusiConsumePlayStore(purchase)
            },
            onFailure = { e ->
                activity.showErrorToast("Gagal update Firestore: ${e}")
                // Fallback jika internet mati / firestore error tetap gunakan nilai standar
                val tokenFallback = purchase.quantity * 25L
                updateTokenCount(purchase, tokenFallback)

                eksekusiConsumePlayStore(purchase)
            }
        )
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

    private fun updateTokenCount(purchase: Purchase, tokenTambahan: Long) {
        val activity = getActivity() ?: return
        activity.viewModel.addBeliToken(tokenTambahan)
        activity.viewModel.getAsesor { asesor ->
            asesor.asesi += purchase.quantity
            activity.config.asesiCount = asesor.asesi
            activity.viewModel.updateAsesor(asesor)
        }
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
        executePurchaseFlow("token_")
    }

}

