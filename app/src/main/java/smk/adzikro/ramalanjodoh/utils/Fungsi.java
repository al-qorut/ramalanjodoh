package smk.adzikro.ramalanjodoh.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;

import smk.adzikro.ramalanjodoh.R;
import smk.adzikro.ramalanjodoh.data.DataBaseHelper;


/**
 * Created by server on 4/25/16.
 */
public class Fungsi {

    public static String[] abjad= {" ","A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X","Y","Z"

    };
    public static String[] huruf_ar= {"ا","ب", "ج", "د", "ه", "و", "ز",
            "ح", "ط", "ي", "ك", "ل", "م",
            "ن", "س", "ع", "ف", "ص", "ق",
            "ر", "ش", "ت", "ث", "خ", "ذ","ض","ظ","غ"

    };
    public static int[] nilai_ar= {1, 2, 3, 4, 5, 6,7, 8, 9,
            10, 20, 30,40, 50, 60, 70, 80, 90,
            100, 200, 300, 400, 500, 600,700,800,900,1000

    };
    public static int[] nilai= {0,1, 2, 3, 4, 5, 6,7, 8, 9,
            10, 20, 30,40, 50, 60, 70, 80, 90,
            100, 200, 300, 400, 500, 600,700,800

    };
    public static int[] img_about = {
            R.drawable.b1, //mandiri 0
            R.drawable.b2, //bjb 1
            R.drawable.b3, //bni 2
            R.drawable.b4, //bri 3
            R.drawable.b5, //adzikro 4
            R.drawable.b6, //smk 5
            R.drawable.b7, //khalasa 6
            R.drawable.b8, //miftah 7
            R.drawable.i0, //8
            R.drawable.b9, //nasri 9
            R.drawable.b10, //nasri 10
            R.drawable.b11, //mar'at 11
            R.drawable.paud, //12
            R.drawable.mts//13
    };
    public static int[] img = {
            R.drawable.i0,
            R.drawable.i1,
            R.drawable.i2,
            R.drawable.i3,
            R.drawable.i4,
            R.drawable.i5,
            R.drawable.i6,
            R.drawable.i7,
            R.drawable.i8,
            R.drawable.i9
    };
    public static int[] img_info = {
            R.drawable.adzikro,
            R.drawable.ikmal
    };
    public static boolean isArabic(String a){
        boolean f=false;
        if(a.length()>1){
            f=true;
        }else {
            for (int i = 0; i < huruf_ar.length; i++) {
                if (huruf_ar[i].equals(a)) {
                    f = true;
                    Log.e("I-getNilaiIndex", "" + f);
                    break;
                }
            }
        }
        return f;
    }
    private static int getNilaiIndex(String a){
        int f=0;
        for(int i=0; i<abjad.length;i++){
            if(abjad[i].equals(a)){
                f= nilai[i];
                Log.e("I-getNilaiIndex",""+f);
                break;
            }
        }
        return f;
    }

    private static int getNilaiIndexAr(String a){
        int f=0;
        for(int i=0; i<huruf_ar.length;i++){
            if(huruf_ar[i].equals(a)){
                f= nilai_ar[i];
             //   Log.e("I-getNilaiIndex",""+f);
                break;
            }
        }
        return f;
    }

    public static int getNilaiAr(String nama){
       // String n = nama.toUpperCase();
        int hasil=0;
        for(int i=0;i<nama.length()-1;i++){
            hasil =hasil+getNilaiIndexAr(nama.substring(i,i+1));

        }
        Log.e("I-getNilai",nama+"-"+hasil);
        return hasil;
    }

    public static int getHasilAr(String nama_wanita, String nama_laki){
        int wanita = getNilaiAr(nama_wanita);
        int laki = getNilaiAr(nama_laki);
        int hasil = (wanita +laki) % 8;
        return hasil;
    }
    public static int getNilai(String nama){
        String n = nama.toUpperCase();
        int hasil=0;
        for(int i=0;i<n.length()-1;i++){
            hasil =hasil+getNilaiIndex(n.substring(i,i+1));

        }
    //    Log.e("I-getNilai",n+"-"+hasil);
        return hasil;
    }

    public static int getHasil(String nama_wanita, String nama_laki){
        int wanita = getNilai(nama_wanita);
        int laki = getNilai(nama_laki);
        if(wanita==0) {
            wanita = getNilaiAr(nama_wanita);
        }
        if(laki==0) {
            laki = getNilaiAr(nama_laki);
        }
        int hasil = (wanita +laki) % 8;
        return hasil;
    }
    public static void share(Context context,CharSequence isi){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, context.getApplicationInfo().loadLabel(context.getPackageManager()).toString());
            String sAux = (String) isi;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            context.startActivity(Intent.createChooser(i,context.getText(R.string.pilih)));
        } catch (Exception e) { //e.toString();
        }
    }

    public static String getInfoHasilAr(Context context,int has, String laki, String wanita){
        String hasil=context.getString(R.string.hasil);//"Pasangan antara %s dengan %s ";
        String hhh="";
        switch (has){
            case 0:
                hhh=String.format(hasil, laki, wanita, context.getString(R.string.hasil0));
                break;
            //i1
            case 1:
                hhh=String.format(hasil, laki, wanita, context.getString(R.string.hasil1));
                break;
            //i6
            case 2:
                hhh=String.format(hasil, laki, wanita, context.getString(R.string.hasil2));
                break;
            //i0
            case 3:
                hhh=String.format(hasil, laki, wanita, context.getString(R.string.hasil3));
                break;
            //i9
            case 4:
                hhh=String.format(hasil, laki, wanita, context.getString(R.string.hasil4));
                break;
            //i4
            case 5:
                hhh=String.format(hasil, laki, wanita, context.getString(R.string.hasil5));
                break;
            //i3
            case 6:
                hhh=String.format(hasil, laki, wanita, context.getString(R.string.hasil6));
                break;
            //5
            case 7:
                hhh=String.format(hasil, laki, wanita, context.getString(R.string.hasil7));
                break;
            //i1
        }
        return hhh;
    }
    public static String getInfoHasilArGb(int has){
        String hasil="0";
        switch (has){
            case 0:
                hasil="1";
                break;
            //i1
            case 1:
                hasil="6";
                break;
            //i6
            case 2:
                hasil="0";
                break;
            //i0
            case 3:
                hasil="9";
                break;
            //i9
            case 4:
                hasil="4";
                break;
            //i4
            case 5:
                hasil="3";
                break;
            //i3
            case 6:
                hasil="5";
                break;
            //5
            case 7:
                hasil="1";
                break;
            //i1
        }
        return hasil;
    }

    public static String getInfoHasil(int has){
        String hasil="Pasangan antara %s dengan %s ";
        switch (has){
            case 0:
                hasil=hasil+" akan mendapat kesuksesan ";
                break;
            case 1:
                hasil=hasil+" akan mendapat kebahagian ";
                break;
            case 2:
                hasil=hasil+" pasangan yang serasi ";
                break;
            case 3:
                hasil=hasil+" pasangan barokah ";
                break;
            case 4:
                hasil=hasil+" awet rajet (bertengkar terus)";
                break;
            case 5:
                hasil=hasil+" tidak cocok ";
                break;
            case 6:
                hasil=hasil+" hidupnya akan susah ";
                break;
            case 7:
                hasil=hasil+" terjadi selingkuh ";
                break;
            case 8:
                hasil=hasil+" salah satunya merasa tertipu ";
                break;
            case 9:
                hasil=hasil+" galau terus.. ";
                break;
        }
        return hasil;
    }
/*
    public static void share(Context context,String path_gb){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Ramalan Jodoh");
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path_gb)));
            String sAux ="Coba Ramal Jodohmu\n "+context.getString(R.string.app_link);
            i.putExtra(Intent.EXTRA_TEXT, sAux);

            context.startActivity(Intent.createChooser(i, "Pilih.."));
        } catch (Exception e) { //e.toString();
        }
    } */

    public static void tambah(Context context, String pasangan, String ihasil, String hasil ){
        SQLiteDatabase db = new DataBaseHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pasangan",pasangan);
        contentValues.put("ihasil",ihasil);
        contentValues.put("hasil",hasil);
        db.insert("ramal",null,contentValues);
        db.close();
    }

    public static void ShowMessage(final Context context, final String judul, final String isi) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(judul);
        builder.setMessage(isi)
                .setCancelable(false)
                /*.setPositiveButton("Share", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                               share(context,);
                                //ShareScreenshot shareScreenshot = new ShareScreenshot(context);
                                //shareScreenshot.saveImage();
                            }
                        }) */
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public static ArrayList<HashMap<String,String>> getRecent(Context context){
        ArrayList<HashMap<String, String>> alist = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = new DataBaseHelper(context).getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from ramal", null);
        //  cursor.moveToFirst();
        if(cursor!=null){
            while (cursor.moveToNext()){
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap.put("no", String.valueOf(cursor.getPosition()));
                hashMap.put("pasangan",cursor.getString(cursor.getColumnIndex("pasangan")));
                hashMap.put("hasil",cursor.getString(cursor.getColumnIndex("hasil")));
                hashMap.put("gb",cursor.getString(cursor.getColumnIndex("ihasil")));
                alist.add(hashMap);
            }
        }
        cursor.close();
        db.close();
        return alist;
    }

    public static void getDelete(Context context){
        SQLiteDatabase db = new DataBaseHelper(context).getWritableDatabase();
        db.execSQL("delete from ramal");
        db.close();
    }


}
