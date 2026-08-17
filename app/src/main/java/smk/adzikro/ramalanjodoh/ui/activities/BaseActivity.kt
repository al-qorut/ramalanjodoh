package smk.adzikro.ramalanjodoh.ui.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.utils.BillingManager
import smk.adzikro.ramalanjodoh.utils.GoogleMobileAdsConsentManager
import smk.adzikro.ramalanjodoh.utils.Progress
import smk.adzikro.ramalanjodoh.utils.toast
import smk.adzikro.ramalanjodoh.viewmodels.MainViewModel
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    private val TAG = "BaseActivity"

    val viewModel by viewModels<MainViewModel>()
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var adView: AdView? = null
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading: Boolean = false
    var v: View? = null
    private val _isLoaded = MutableLiveData<Boolean>()
    val isLoaded: LiveData<Boolean> get() = _isLoaded
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    @Inject
    lateinit var billing: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { error ->
            if (error != null) {
                Log.e(TAG, "${error.errorCode}: ${error.message}")
            }
            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                invalidateOptionsMenu()
            }
        }
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }
        _isLoaded.value = false

        billing.initActivity(this)
        billing.startBillingConnection()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Pastikan tampilan sudah siap sebelum mengakses insetsController
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.statusBars()) // Menyembunyikan status bar
                    controller.hide(WindowInsets.Type.navigationBars()) // Menyembunyikan navigation bar
                }
            } else {
                // Untuk versi lebih lama, tetap menggunakan system UI flags
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE
            }
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    fun beliToken() {
        billing?.beliToken()
    }

    private val adSize: AdSize
        get() {
            val displayMetrics = resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    fun loadBanner() {
        val adView = AdView(this)
        adView.adUnitId = AD_UNIT_ID
        adView.setAdSize(adSize)
        this.adView = adView
        v?.visibility = View.VISIBLE
        (v as? ViewGroup)?.removeAllViews()
        (v as? ViewGroup)?.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                _isLoaded.value = true
                // Iklan berhasil dimuat
                Log.d("AdBanner", "Iklan berhasil dimuat!")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                _isLoaded.value = false
                // Iklan gagal dimuat
                Log.d("AdBanner", "Iklan gagal dimuat: ${loadAdError.message}")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                // Iklan dibuka
                Log.d("AdBanner", "Iklan dibuka!")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                // Iklan diklik
                Log.d("AdBanner", "Iklan diklik!")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                // Iklan ditutup
                Log.d("AdBanner", "Iklan ditutup!")
            }
        }
        adView.loadAd(adRequest)
    }


    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Set your test devices.
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf(getString(R.string.id_admob)))
                .build()
        )

        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@BaseActivity) {}

            runOnUiThread {
                // Load an ad on the main thread.
                if (v != null) {
                    loadBanner()
                }
                loadAd()
                loadRewardedAds()
            }
        }
    }
    /*
    * interstitialAs
    *
    * */


    private fun loadAd() {
        if (adIsLoading || interstitialAd != null) {
            return
        }
        adIsLoading = true

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            AD_UNIT_TRS,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    interstitialAd = null
                    adIsLoading = false
                    _isLoaded.value = false
                    val error =
                        "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
                    Log.e(TAG, error)
                    /*Toast.makeText(
                        this@BaseActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT,
                    )
                        .show() */
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                    _isLoaded.value = true
                    // Toast.makeText(this@BaseActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                }
            },
        )
    }

    fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        interstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(TAG, "Ad failed to show.")
                        interstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                    }
                }
            interstitialAd?.show(this)
        } else {
            if (googleMobileAdsConsentManager.canRequestAds) {
                loadAd()
            }
        }
    }


    private fun loadRewardedAds() {
        if (rewardedAd == null) {
            isLoading = true
            var adRequest = AdManagerAdRequest.Builder().build()
            //val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                this,
                AD_REWARD_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(mRewardedAd: RewardedAd) {
                        isLoading = false
                        rewardedAd = mRewardedAd
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        isLoading = false
                        rewardedAd = null
                    }
                }
            )
        }
    }

    private fun addToken(token: Int) {
        viewModel.addBeliToken(
            token.toLong(),
            onSuccess = {
                toast(this, getString(R.string.bonus_token))
            },
            onFailure = { e ->
                toast(this, getString(R.string.error, e))
            })
    }

    fun showRewadedAds() {
        if (rewardedAd == null && !isLoading && googleMobileAdsConsentManager.canRequestAds) {
            loadRewardedAds()
            Toast.makeText(this, "Coba ulangi", Toast.LENGTH_LONG).show()
        } else {
            showRewardshowRewardedVideo()
        }
    }

    private fun showRewardshowRewardedVideo() {
        if (rewardedAd != null) {
            rewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadRewardedAds()
                    }

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    rewardedAd = null
                }

            }
            rewardedAd!!.show(this) {
                addToken(it.amount)
            }
        } else {
            // Toast.makeText(this, "Ads is Loaded", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Ads is Loaded")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adView = null
        interstitialAd = null
        rewardedAd = null
    }

    override fun onStop() {
        super.onStop()
        adView?.pause()
        //interstitialAd.
        //rewardedAd.
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }
    private var progres: Progress? = null
    fun onProgress(message: String, state: Boolean) {
        runOnUiThread {
            if (state) {
                if (progres == null) {
                    progres = Progress(this, message, cancelable = true)
                } else {
                    progres?.setInfo(message)
                }
                progres?.show()
            } else {
                // Jika state false, tutup dialog dan kosongkan kembali variabelnya
                progres?.dismiss()
                progres = null
            }
        }
    }

    companion object {
        private const val AD_REWARD_ID = "ca-app-pub-3624492980147085/8048807607"
        private const val AD_APP_ID = "ca-app-pub-3624492980147085~8894706455"
        private const val AD_UNIT_ID = "ca-app-pub-3624492980147085/1371439657"
        private const val AD_UNIT_TRS = "ca-app-pub-3624492980147085/3400975653"
        const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"
    }
}