package smk.adzikro.ramalanjodoh.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by server on 4/30/16.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="ramal.db";
    Context context;
    public DataBaseHelper(Context contextx) {
        super(contextx,DATABASE_NAME,null,1);
        this.context =contextx;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ramal(pasangan text, ihasil int, hasil text)");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
