package smk.adzikro.ramalanjodoh

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_dialog.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import smk.adzikro.ramalanjodoh.MainActivity
import smk.adzikro.ramalanjodoh.activity.AboutActivity
import smk.adzikro.ramalanjodoh.activity.Recent
import smk.adzikro.ramalanjodoh.data.DataBaseHelper
import smk.adzikro.ramalanjodoh.utils.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {
    //To upload an Android App Bundle you must be enrolled in Play app signing.
    var click = 0
    var hitung = false
    private var mInterstitialAd: InterstitialAd? = null
    private val TAG = "MainActivity"
    private var countDownTimer: CountDownTimer? = null
    private var loading: CountDownTimer? = null
    private var gameIsInProgress = false
    private var timerMilliseconds: Long = 0
    private var kata: ArrayList<String>? = null
    private val lis  = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        proses.setOnClickListener(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.setAdListener(object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                Log.e(TAG, "onAdClosed ")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.e(TAG, "Error Iklan $loadAdError")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.e(TAG, "onAdOpened ")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.e(TAG, "onAdLoaded ")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.e(TAG, "onAdCliked ")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.e(TAG, "onAdImpession ")
            }
        })
        loadAd()
        GlobalScope.launch {
            loadKata()
            buatList()
        }

    }

    private fun loadKata() {

        val s = Scanner(resources.openRawResource(R.raw.katax))
        kata = ArrayList()
        try {
            while (s.hasNext()) {
                kata!!.add(s.next())
            }
        } finally {
            s.close()
        }
    }


    fun loadAd() {
        Log.e(TAG, "Preparing Interestial")
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,
            getString(R.string.interstitial_ad_unit_id),
            adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    //super.onAdLoaded(interstitialAd);
                    mInterstitialAd = interstitialAd
                    Log.e(TAG, "interstitialAd onLoaded")
                    mInterstitialAd!!.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                //super.onAdFailedToShowFullScreenContent(adError);
                                Log.e(TAG, "onAdFailedToShowFullScreenContent")
                                mInterstitialAd = null
                            }

                            override fun onAdShowedFullScreenContent() {
                                //super.onAdShowedFullScreenContent();
                                Log.e(TAG, "onAd was ToShowFullScreenContent")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                //super.onAdDismissedFullScreenContent();
                                mInterstitialAd = null
                                Log.e(TAG, "onAdDismissToShowFullScreenContent")
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    //super.onAdFailedToLoad(loadAdError);
                    mInterstitialAd = null
                    Log.e(TAG, "onAdFailedToLoad $loadAdError")
                }
            })
    }

    override fun onClick(v: View) {
        if (hitung) {
            reset()
        } else {
            val jalux = jalu.text.toString()
            val bikangx = bikang.text.toString()
           // Log.e(TAG, "inpotxx $jalux $bikangx")
            if (jalux!!.isEmpty() || bikangx!!.isEmpty()) {
                return
            }
            if (jalux.equals(bikangx)) {
                showMessage(String.format(getString(R.string.sama), jalux, bikangx))
                return
            }
            if (kata!!.contains(jalux) || kata!!.contains(bikangx)) {
                showMessage(String.format(getString(R.string.ada),jalux, bikangx))
                return
            }
            if(cekHuruf(jalux) || cekHuruf(bikangx)){
                showInfo(getString(R.string.tdbenar))
                return
            }
            if (jalu!!.text.toString().length < 3 || bikang!!.text.toString().length < 3) return
            hitung()
        }
    }
    private fun showInfo(s: String) {
        AlqDialog.build(this)
            .icon(R.drawable.ic_tanya,true)
            .title(s)
            .onPositive(getString(R.string.ok)){
                reset()
            }
            .show()
    }

    private fun showMessage(s: String) {
       AlqDialog.build(this)
           .icon(R.drawable.ic_tanya,true)
           .title(s)
           .onPositive(getString(R.string.ok)){
               hitung()
           }
           .onNegative(getString(R.string.no)){
               reset()
           }
           .show()
    }
    private fun buatList(){
        var c = 'A'
        while (c <='Z'){
            var x =""
            for (i in 1..3){
                x=x+c
            }
            lis?.add(x)
            ++c
        }
    }
    private fun cekHuruf(huruf:String):Boolean{
        val h = huruf.uppercase()
        var hasil = false
        lis.forEach {
            if (h.contains(it, true))
                hasil = true
        }
        return hasil
    }

    fun reset() {
        jalu.setText("")
        bikang.setText("")
        view_hasil.visibility = View.GONE
        hasil_text.text = ""
        imageView2.setImageResource(R.drawable.bg)
        textHitung.text = getString(R.string.action_hitung)
        hitung = false
        click++
        if (click >= 2) {
            showInterstitial()
            click = 0
        }
    }

    var naw: String? = null
    var nal: String? = null
    var h = 0

    fun hitung() {
        hitung = true
        proses.visibility = View.INVISIBLE
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        imageView2.startAnimation(pulseAnimation)
        naw = bikang!!.text.toString()
        nal = jalu!!.text.toString()
        h = Fungsi.getHasil(naw, nal)
        hitungJodoh(10000)
        loading!!.start()
    }

    fun tampil() {
        val has = Fungsi.getInfoHasilAr(this, h, nal, naw)
        view_hasil.visibility = View.VISIBLE
        hasil_text.text = has
        imageView2.setImageResource(Fungsi.img[Integer.valueOf(Fungsi.getInfoHasilArGb(h))])
        tambah(
            getString(R.string.app_name),
            Fungsi.getInfoHasilArGb(h),
            String.format(has, naw, nal)
        )
        imageView2.clearAnimation()
    }

    fun tambah(pasangan: String?, ihasil: String?, hasil: String?) {
        val db = DataBaseHelper(this).writableDatabase
        val contentValues = ContentValues()
        contentValues.put("pasangan", pasangan)
        contentValues.put("ihasil", ihasil)
        contentValues.put("hasil", hasil)
        db.insert("ramal", null, contentValues)
        db.close()
    }

    private val applicationName: String
        private get() {
            val pm = applicationContext
                .packageManager
            val ai: ApplicationInfo
            var appName: String
            try {
                ai = pm.getApplicationInfo(packageName, 0)
                appName = pm.getApplicationLabel(ai) as String
            } catch (e: PackageManager.NameNotFoundException) {
                appName = "(unknown)"
            }
            return appName
        }

    private val isPlayStoreInstalled: Boolean
        private get() {
            val pacman = packageManager
            return try {
                pacman.getApplicationInfo("com.android.vending", 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

    private fun showRate() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri
                        .parse(
                            "market://details?id="
                                    + packageName
                        )
                )
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this, getString(R.string.no_play_store),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun rate() {
        Log.e(TAG, "Show rate")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.rate))
        builder.setMessage(String.format(getString(R.string.title_rate), applicationName))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, which -> showRate() }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which -> }.show()
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this)
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            startCount()
        }
    }

    private fun startCount() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (mInterstitialAd == null) {
            loadAd()
        }

        //  retryButton.setVisibility(View.INVISIBLE);
        resumeCount(COUNT_LENGTH_MILLISECONDS)
    }

    private fun createTimer(milliseconds: Long) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }

        //  final TextView textView = findViewById(R.id.timer);
        countDownTimer = object : CountDownTimer(milliseconds, 50) {
            override fun onTick(millisUnitFinished: Long) {
                timerMilliseconds = millisUnitFinished
                //   textView.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            override fun onFinish() {
                gameIsInProgress = false
                // textView.setText("done!");
            }
        }
    }

    private fun hitungJodoh(milliseconds: Long){
        if (loading !=null){
            loading!!.cancel()
        }
        loading = object : CountDownTimer(milliseconds, 50){
            override fun onTick(millisUntilFinished: Long) {
              //  Log.e(TAG,"seconds remaining: " + (millisUntilFinished / 1000) + 1)
            }

            override fun onFinish() {
                tampil()
                proses.visibility = View.VISIBLE
                textHitung.setText(getString(R.string.action_lagi))
            }
        }
    }

    private fun resumeCount(milliseconds: Long) {
        // Create a new timer for the correct length and start it.
        gameIsInProgress = true
        timerMilliseconds = milliseconds
        createTimer(milliseconds)
        countDownTimer!!.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        // menu.findItem(R.id.action_arab).setChecked(Fungsi.isHikmah(this));
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            /*PackageManager manager = getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                String versi = info.versionName;
                Fungsi.ShowMessage(this,"About","Ramalan Jodoh Ver "+versi+" \ndev SMK ADZ-DZIKRO BANDUNG\nFB Pesantren Adzikro Twitter @alqorut");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } */
            startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            return true
        } else if (id == R.id.action_rate) {
            rate()
            return true
        } else if (id == R.id.action_share) {
            Fungsi.share(
                this,
                "$applicationName\n \nhttps://play.google.com/store/apps/details?id=smk.adzikro.ramalanjodoh \n"
            )
            return true
        } else if (id == R.id.action_exit) {
            finish()
            return true
        } /*else if (id == R.id.action_info) {
            startActivity(new Intent(MainActivity.this, InfoActivity.class));
            return true;
        } */ else if (id == R.id.action_recent) {
            startActivity(Intent(this@MainActivity, Recent::class.java))
            return true
        } else if (id == R.id.action_aufaq) {
            // startActivity(new Intent(MainActivity.this, HitungAlaufaq.class));
            val appPackageName =
                "SMK ADZ-DZIKRO" // getPackageName() from Context or Activity object
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://search?q=pub:$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/search?q=pub:$appPackageName")
                    )
                )
            }
            return true
        } /*else if (id == R.id.action_help) {
            Fungsi.ShowMessage(this, getString(R.string.help),getString(R.string.infohelp));
            return true;
        }*/
        return super.onOptionsItemSelected(item)
    }



    companion object {
        private const val COUNT_LENGTH_MILLISECONDS: Long = 2000
    }
}