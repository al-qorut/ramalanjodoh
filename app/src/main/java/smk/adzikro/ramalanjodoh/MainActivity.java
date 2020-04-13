package smk.adzikro.ramalanjodoh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.textfield.TextInputEditText;

import smk.adzikro.ramalanjodoh.activity.AboutActivity;
import smk.adzikro.ramalanjodoh.activity.InfoActivity;
import smk.adzikro.ramalanjodoh.activity.Recent;
import smk.adzikro.ramalanjodoh.data.DataBaseHelper;
import smk.adzikro.ramalanjodoh.utils.Fungsi;
import smk.adzikro.ramalanjodoh.utils.RateMeMaybe;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, RateMeMaybe.OnRMMUserChoiceListener  {
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
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        iklanInter();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
                Log.e("Ad"," "+errorCode);
                 super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
                  super.onAdLoaded();
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

    private void rate() {
        RateMeMaybe.resetData(MainActivity.this);
        RateMeMaybe rmm = new RateMeMaybe(MainActivity.this);
        rmm.setPromptMinimums(0, 0, 0, 0);
        rmm.setRunWithoutPlayStore(true);
        rmm.setAdditionalListener(this);
        rmm.setDialogMessage("Kalau suka dan bermanfaat, "
                //    +"telah menjalankan %totalLaunchCount% kali! "
                + "silahkan rate aplikasinya");
        rmm.setDialogTitle("Rate..");
        rmm.setPositiveBtn("OK!");
        rmm.run();
    }


    private void iklanInter() {
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //  startActivity(new Intent(FragmenTerjemah.this,TemaAyatActivity.class));
                onResume();
               reqIklan();
                // Toast.makeText(FragmenTerjemah.this, "Versi Iklhas bisa offline dan tidak ada Iklan, Silahkan upgrade ", Toast.LENGTH_SHORT).show();
            }
        });
        reqIklan();
    }
    private void reqIklan(){
      if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
           AdRequest adRequest = new AdRequest.Builder().build();
           mInterstitialAd.loadAd(adRequest);
         }
    }
    private void showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
             click=0;
            }
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
            Fungsi.share(this,"Aplikasi Ramalan  \n \nhttps://play.google.com/store/apps/details?id=smk.adzikro.ramalanjodoh \n");
            return true;
        }else if (id == R.id.action_exit) {
            this.finish();
            return true;
        }else if (id == R.id.action_info) {
            startActivity(new Intent(MainActivity.this, InfoActivity.class));
            return true;
        }else if (id == R.id.action_recent) {
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
        }else if (id == R.id.action_help) {
            Fungsi.ShowMessage(this, getString(R.string.help),getString(R.string.infohelp));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handlePositive() {

    }

    @Override
    public void handleNeutral() {

    }

    @Override
    public void handleNegative() {

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
