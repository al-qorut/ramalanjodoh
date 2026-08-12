package smk.adzikro.ramalanjodoh.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smk.adzikro.ramalanjodoh.ui.activities.BaseActivity

class BillingManager(private val context: Context) {
    private var billingClient: BillingClient? = null
    var productDetailsList = mutableListOf<ProductDetails>()

    fun startBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Proses pembelian
                    purchases?.forEach { purchase ->
                        handlePurchase(purchase)
                    }
                }
            }
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Koneksi berhasil
                    //queryPurchases()
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Tangani jika billing service terputus
            }
        })
    }
    private val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .enableOneTimeProducts()
        .build()

    fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            verifyPurchaseToken(purchase.purchaseToken)
        }
    }

    fun acknowledgePurchase(purchaseToken: String) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams, object :
            AcknowledgePurchaseResponseListener {
            override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    (context as Activity).runOnUiThread {
                        (context as BaseActivity).viewModel.addBeliToken(5)
                    }
                }
            }
        })
    }
    fun showProducts() {
        val productList = mutableListOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("token_prediksi")
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("token_ramal")
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        billingClient?.queryProductDetailsAsync(
            params,
            ProductDetailsResponseListener { billingResult: BillingResult?, list: MutableList<ProductDetails> ->
                productDetailsList.clear()
                CoroutineScope(Dispatchers.IO).launch {
                    productDetailsList.addAll(list)
                }

            })
    }

    fun beliToken(){
        if(billingClient != null){
            val productDetailsParamsList = mutableListOf<BillingFlowParams. ProductDetailsParams>(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetailsList.get(1))
                    .build()
            )

            val params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient?.launchBillingFlow(context as Activity, params)
        }
    }

    fun verifyPurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val listener =
            ConsumeResponseListener { billingResult: BillingResult, s: String? ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    (context as Activity).runOnUiThread {
                        (context as BaseActivity).viewModel.addBeliToken(5)
                    }
                }else{
                    Log.d("Purchase", "Gagal mengkonsumsi produk.")
                }
            }
        billingClient?.consumeAsync(consumeParams, listener)
    }

    fun verifyPurchaseToken(purchaseToken: String) {
        // Menjalankan queryPurchasesAsync untuk mendapatkan pembelian yang ada
        billingClient?.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var isConsumed = false

                // Loop untuk memeriksa apakah purchaseToken sudah ada dalam daftar pembelian
                for (purchase in purchasesList) {
                    if (purchase.purchaseToken == purchaseToken) {
                        if (purchase.isAcknowledged) {
                            // Jika produk sudah dikonsumsi atau diakui
                            isConsumed = true
                        } else {
                            // Jika produk belum diakui, bisa mengkonsumsi produk tersebut
                            consumePurchase(purchase)
                        }
                    }
                }

                if (isConsumed) {
                    Log.d("Purchase", "Item sudah dikonsumsi atau diakui sebelumnya.")
                } else {
                    Log.d("Purchase", "Item belum dikonsumsi.")
                }
            } else {
                Log.e("Purchase", "Gagal mengambil data pembelian: ${billingResult.debugMessage}")
            }
        }
    }

    fun consumePurchase(purchase: Purchase) {
        // Jika produk belum dikonsumsi, maka lakukan konsumsi produk
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val listener = ConsumeResponseListener { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d("Consume", "Produk berhasil dikonsumsi.")
                val x = purchase.quantity * 5
                (context as Activity).runOnUiThread {
                    (context as BaseActivity).viewModel.addBeliToken(x.toLong())
                }
            } else {
                Log.d("Consume", "Gagal mengkonsumsi produk.")
            }
        }

        billingClient?.consumeAsync(consumeParams, listener)
    }


}
