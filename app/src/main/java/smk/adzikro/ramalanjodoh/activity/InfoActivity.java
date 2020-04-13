package smk.adzikro.ramalanjodoh.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import smk.adzikro.ramalanjodoh.R;
import smk.adzikro.ramalanjodoh.utils.Fungsi;


public class InfoActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<HashMap<String,Integer>> alist;
    listAdapter adapter;
    @Override
    protected void onCreate(Bundle bundle){
       super.onCreate(bundle);
        setContentView(R.layout.recent);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.list_item);
        alist = getInfo();
        adapter = new listAdapter(this,R.layout.item_info,alist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(alist.get(position).get("web")))));
            }
        });
   }

    private ArrayList<HashMap<String,Integer>> getInfo(){
        ArrayList<HashMap<String, Integer>> alist = new ArrayList<HashMap<String, Integer>>();
        //adzikro
        HashMap<String,Integer> hashMap = new HashMap<String,Integer>();
        hashMap.put("img", 0);
        hashMap.put("info",R.string.infoadzikro);
        hashMap.put("web",R.string.webadzikro);
        alist.add(hashMap);
        //ikmal
        HashMap<String,Integer> hashMap1 = new HashMap<String,Integer>();
        hashMap1.put("img", 1);
        hashMap1.put("info",R.string.infoikmal);
        hashMap1.put("web",R.string.webikmal);
        alist.add(hashMap1);
        return alist;
    }

    class listAdapter extends ArrayAdapter<HashMap<String, Integer>>{
        Context ctx;
        ArrayList<HashMap<String, Integer>> list;

        public listAdapter(@NonNull Context context, int resource, ArrayList<HashMap<String, Integer>> list) {
            super(context, resource);
            this.ctx = context;
            this.list = list;
        }

        public int getCount(){
            return list.size();
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            Holder holder = null;
           // Log.e("Info", "naon "+getString(list.get(pos).get("info")));
            LayoutInflater inflater = ((Activity) ctx)
                    .getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_info,
                    parent, false);
            holder = new Holder();
            holder.img = convertView.findViewById(R.id.image);
            holder.info = convertView.findViewById(R.id.info);
            holder.info.setText(getString(list.get(pos).get("info"))+"\n"+getString(list.get(pos).get("web")));
            int i = list.get(pos).get("img");
            holder.img.setImageResource(Fungsi.img_info[i]);
            return convertView;
        }
        class Holder{
            TextView info;
            ImageView img;
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
