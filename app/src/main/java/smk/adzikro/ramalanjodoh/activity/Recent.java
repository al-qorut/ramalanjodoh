package smk.adzikro.ramalanjodoh.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import smk.adzikro.ramalanjodoh.R;
import smk.adzikro.ramalanjodoh.utils.Fungsi;


/**
 * Created by server on 4/30/16.
 */
public class Recent extends AppCompatActivity {
    CustomListAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.list_item);
        ArrayList<HashMap<String, String>> alist = Fungsi.getRecent(this);
        adapter = new CustomListAdapter(this,R.layout.content_recent,alist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             showMessage();
            }
        });
    }
    public void showMessage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.hapus));
        builder.setMessage(getString(R.string.tanya_hapus))
                .setCancelable(false)
                .setPositiveButton(R.string.hapus, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Fungsi.getDelete(Recent.this);
                                adapter.notifyDataSetChanged();
                                listView.setAdapter(null);
                            }
                        })
                .setNegativeButton(R.string.tidak, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    class CustomListAdapter extends ArrayAdapter<HashMap<String, String>> {
        Context context;
        int textViewResourceId;
        ArrayList<HashMap<String, String>> alist;

        public CustomListAdapter(Context context, int textViewResourceId,
                                 ArrayList<HashMap<String, String>> alist) {
            super(context, textViewResourceId);
            this.context = context;
            this.alist = alist;
            this.textViewResourceId = textViewResourceId;

        }

        public int getCount() {

            return alist.size();
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            Holder holder = null;

            LayoutInflater inflater = ((Activity) context)
                    .getLayoutInflater();
            convertView = inflater.inflate(R.layout.content_recent,
                    parent, false);
            holder = new Holder();
            holder.no = (TextView) convertView
                    .findViewById(R.id.no);
            holder.pasangan = (TextView) convertView
                    .findViewById(R.id.pasangan);
            holder.hasil = (TextView) convertView
                    .findViewById(R.id.hasil);
            holder.photo = (ImageView) convertView
                    .findViewById(R.id.img_photo);
            holder.lin_background = (RelativeLayout) convertView
                    .findViewById(R.id.linear);
            convertView.setTag(holder);


            holder = (Holder) convertView.getTag();

            holder.no.setText(alist.get(pos).get("no"));
            holder.pasangan.setText(alist.get(pos).get("pasangan"));
            holder.hasil.setText(alist.get(pos).get("hasil"));
            int i = Integer.parseInt(alist.get(pos).get("gb"));
            holder.photo.setImageResource(Fungsi.img[i]);
            return convertView;

        }

        class Holder {
            TextView no, pasangan, hasil;
            ImageView photo;
            RelativeLayout lin_background;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
