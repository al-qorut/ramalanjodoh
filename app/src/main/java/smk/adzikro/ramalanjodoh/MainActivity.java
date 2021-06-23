package smk.adzikro.ramalanjodoh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import smk.adzikro.ramalanjodoh.activity.AboutActivity;
import smk.adzikro.ramalanjodoh.activity.Recent;
import smk.adzikro.ramalanjodoh.data.DataBaseHelper;
import smk.adzikro.ramalanjodoh.utils.Fungsi;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {
    TextInputEditText jalu;
    TextInputEditText bikang;
    CardView hasil_view, proses;
    InputMethodManager imm;
    TextView hasil_text;
    TextView proses_text;
    ImageView img_hasil;
    Integer click=0;
    boolean hitung = false;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private final String TAG="MainActivity";
    private static final long COUNT_LENGTH_MILLISECONDS = 3000;
    private CountDownTimer countDownTimer;
    private boolean gameIsInProgress;
    private long timerMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jalu = findViewById(R.id.jalu);
        bikang = findViewById(R.id.bikang);
        proses = findViewById(R.id.proses);
        hasil_text = findViewById(R.id.hasil_text);
        proses_text = findViewById(R.id.textHitung);
        img_hasil = findViewById(R.id.imageView2);
        hasil_view = findViewById(R.id.view_hasil);
        imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
        proses.setOnClickListener(this);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG, "onAdClosed ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "Error Iklan "+loadAdError.toString());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.e(TAG, "onAdOpened ");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(TAG, "onAdLoaded ");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.e(TAG, "onAdCliked ");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.e(TAG, "onAdImpession ");
            }
        });

        loadAd();
    }
    public void loadAd(){
        Log.e(TAG, "Preparing Interestial");
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,
                getString(R.string.interstitial_ad_unit_id),
                adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                        //super.onAdLoaded(interstitialAd);
                        mInterstitialAd = interstitialAd;
                        Log.e(TAG, "interstitialAd onLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                                         @Override
                                                                         public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                                                                             //super.onAdFailedToShowFullScreenContent(adError);
                                                                             Log.e(TAG, "onAdFailedToShowFullScreenContent");
                                                                             mInterstitialAd = null;
                                                                         }

                                                                         @Override
                                                                         public void onAdShowedFullScreenContent() {
                                                                             //super.onAdShowedFullScreenContent();
                                                                             Log.e(TAG, "onAd was ToShowFullScreenContent");
                                                                         }

                                                                         @Override
                                                                         public void onAdDismissedFullScreenContent() {
                                                                             //super.onAdDismissedFullScreenContent();
                                                                             mInterstitialAd = null;
                                                                             Log.e(TAG, "onAdDismissToShowFullScreenContent");
                                                                         }

                                                                     }
                        );
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                        //super.onAdFailedToLoad(loadAdError);
                        mInterstitialAd = null;
                        Log.e(TAG, "onAdFailedToLoad "+loadAdError);
                    }
                });
    }
    @Override
    public void onClick(View v) {
        if(hitung) {
            reset();
            return;
        }
        if(jalu.getText().toString().length()<3 ||bikang.getText().toString().length()<3 )return;
        new getHitung().execute();

    }
    public void reset(){
        jalu.setText("");
        bikang.setText("");
        hasil_view.setVisibility(View.GONE);
        hasil_text.setText("");
        img_hasil.setImageResource(R.drawable.bg);
        proses_text.setText(getString(R.string.action_hitung));
        hitung = false;
        click++;
        if(click>=2){
            showInterstitial();
            click=0;
        }
    }
    String naw, nal;
    int h;
    public void hitung(){
        naw =bikang.getText().toString();
        nal =jalu.getText().toString();
        h = Fungsi.getHasil(naw, nal);
    }

    public void tampil(){
        String has = Fungsi.getInfoHasilAr(this,h, nal, naw);
        hasil_view.setVisibility(View.VISIBLE);
        hasil_text.setText(has);
        img_hasil.setImageResource(Fungsi.img[Integer.valueOf(Fungsi.getInfoHasilArGb(h))]);
        tambah(getString(R.string.app_name), Fungsi.getInfoHasilArGb(h), String.format(has, naw, nal));
    }
    public void tambah(String pasangan, String ihasil, String hasil ){
        SQLiteDatabase db = new DataBaseHelper(this).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pasangan",pasangan);
        contentValues.put("ihasil",ihasil);
        contentValues.put("hasil",hasil);
        db.insert("ramal",null,contentValues);
        db.close();
    }
    private String getApplicationName() {
        final PackageManager pm = getApplicationContext()
                .getPackageManager();
        ApplicationInfo ai;
        String appName;
        try {
            ai = pm.getApplicationInfo(getPackageName(), 0);
            appName = (String) pm.getApplicationLabel(ai);
        } catch (final PackageManager.NameNotFoundException e) {
            appName = "(unknown)";
        }
        return appName;
    }
    private Boolean isPlayStoreInstalled() {
        PackageManager pacman = getPackageManager();
        try {
            pacman.getApplicationInfo("com.android.vending", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private void showRate() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("market://details?id="
                                    + getPackageName())));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_play_store),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void rate() {
        Log.e(TAG, "Show rate");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.rate));
        builder.setMessage(String.format(getString(R.string.title_rate), getApplicationName()));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showRate();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }



    private void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            startCount();
        }
    }
    private void startCount() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (mInterstitialAd == null) {
            loadAd();
        }

      //  retryButton.setVisibility(View.INVISIBLE);
        resumeCount(COUNT_LENGTH_MILLISECONDS);
    }
    private void createTimer(final long milliseconds) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

      //  final TextView textView = findViewById(R.id.timer);

        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
             //   textView.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                gameIsInProgress = false;
               // textView.setText("done!");
            }
        };
    }
    private void resumeCount(long milliseconds) {
        // Create a new timer for the correct length and start it.
        gameIsInProgress = true;
        timerMilliseconds = milliseconds;
        createTimer(milliseconds);
        countDownTimer.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // menu.findItem(R.id.action_arab).setChecked(Fungsi.isHikmah(this));
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            /*PackageManager manager = getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                String versi = info.versionName;
                Fungsi.ShowMessage(this,"About","Ramalan Jodoh Ver "+versi+" \ndev SMK ADZ-DZIKRO BANDUNG\nFB Pesantren Adzikro Twitter @alqorut");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } */
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }else if (id == R.id.action_rate) {
            rate();
            return true;
        }else if (id == R.id.action_share) {
            Fungsi.share(this,getApplicationName()+"\n \nhttps://play.google.com/store/apps/details?id=smk.adzikro.ramalanjodoh \n");
            return true;
        }else if (id == R.id.action_exit) {
            this.finish();
            return true;
        }/*else if (id == R.id.action_info) {
            startActivity(new Intent(MainActivity.this, InfoActivity.class));
            return true;
        } */
        else if (id == R.id.action_recent) {
            startActivity(new Intent(MainActivity.this, Recent.class));
            return true;
        }else if (id == R.id.action_aufaq) {
            // startActivity(new Intent(MainActivity.this, HitungAlaufaq.class));
            final String appPackageName = "SMK ADZ-DZIKRO"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:" + appPackageName)));
            }
            return true;
        }/*else if (id == R.id.action_help) {
            Fungsi.ShowMessage(this, getString(R.string.help),getString(R.string.infohelp));
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }



    class getHitung extends AsyncTask<Void, Integer, Void> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("ramal...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hitung();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            hitung = true;
            proses_text.setText(getString(R.string.action_lagi));
            tampil();
            Toast.makeText(MainActivity.this, "Selesai.. diramal", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            imm.hideSoftInputFromWindow(jalu.getWindowToken(),0);
        }
    }


}
