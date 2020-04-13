package smk.adzikro.ramalanjodoh.activity

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_about.*
import pst.adzikro.doadzikir.models.About
import pst.adzikro.doadzikir.models.ListAbout
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.utils.Fungsi


class AboutActivity : AppCompatActivity() {

    val bank = listOf<About>(
            About(0,"132-00-1532888-4","Bank Mandiri","com.bankmandiri.mandirionline"),
            About(1,"0092109811100","Bank Jabar Banten (BJB)","gurilap.bjbmobile"),
            About(2,"0415939278","Bank Negara Indonesia (BNI)","src.com.bni"),
            About(3,"0887-01-043136-53-7","Bank Rakyat (BRI)","bri.delivery.brimobile"))
    val pst = listOf<About>(
            About(4,"Pesantren Adz-Dzikro","Tugulaksana 02/13 Pagerwangi Lembang Bandung Barat","http://bit.ly/adzikro"),
            About(11,"Pesantren Umi Kultsum","Cianjur\nhttp://umikultsum.ponpes.id/ ","https://forms.gle/ozELBTbcSAqo68By8"),
            About(6,"Pesantren Khalasa","Cijawura Buah Batu Bandung","http://bit.ly/adzikro"),
            About(9,"Pesantren Daarun Nashri","Cilengkrang Bandung","http://bit.ly/adzikro"),
            About(10,"Pesantren Mir'at Alfaqih","Malangbong Garut","http://umikultsum.ponpes.id/")
            )
    val sekolah = listOf<About>(
            About(5,"SMK Adz-Dzikro","Tugulaksana 01/13 Pagerwangi Lembang Bandung Barat","http://bit.ly/adzikro"),
            About(12,"PAUD Adz-Dzikro","Tugulaksana 01/13 Pagerwangi Lembang Bandung Barat","http://bit.ly/adzikro"),
            About(13,"MTs Adz-Dzikro","Tugulaksana 01/13 Pagerwangi Lembang Bandung Barat","http://bit.ly/adzikro"),
            About(7,"PAUD, TK, TPA-TQA, SMP IT Miftahussaadah","Cianjur","http://bit.ly/adzikro")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_about)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        title =getString(R.string.about)
        init()

    }
    private fun getList(): MutableList<ListAbout>? {
        val arrayList: MutableList<ListAbout> = mutableListOf()
        var string :String=""
        for (j in 1..3) {
           when(j) {
               1->string = "Donation"
               2->string = "Pesantren"
               3->string = "Sekolah"
           }
            val header = ListAbout(null, true, string)
            arrayList.add(header)
            when(j){
                1->bank.forEach {
                    arrayList.add(ListAbout(it,false,""))
                }
                2->pst.forEach {
                    arrayList.add(ListAbout(it,false,""))
                }
                3->sekolah.forEach {
                    arrayList.add(ListAbout(it,false,""))
                }
            }
        }
        return arrayList
    }

    private fun setClipboard(context: Context, text: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }
        Toast.makeText(this, text+"\n Copied",Toast.LENGTH_SHORT).show()
    }
    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun startNewActivity(context: Context, packageName: String) {

        val pm: PackageManager = context.getPackageManager()
        val isInstalled = isPackageInstalled(packageName, pm)
        if (!isInstalled) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        }else{
            openApp(this, packageName)
        }
     }

    fun openApp(context: Context, packageName: String?): Boolean {
        val manager = context.packageManager
        return try {
            val i = manager.getLaunchIntentForPackage(packageName!!)
                    ?: return false
            //throw new ActivityNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            context.startActivity(i)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    fun init(){
        val manager = packageManager
        var versi = "1.0.1"
        try {
            val info = manager.getPackageInfo(packageName, 0)
            versi = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
       textView.text = String.format(getString(R.string.tentang), versi)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AboutActivity)
            adapter = ListAboutAdapter(getList()!!)
        }
    }


    inner class ListAboutAdapter(
            private val listA : MutableList<ListAbout>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private val TYPE_HEADER=1
        private val TYPE_ITEM =2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            if(viewType==TYPE_HEADER) {
                return HolderHeader(inflater.inflate(R.layout.item_header, parent, false))
            }else{
                return HolderAbout(inflater.inflate(R.layout.item_about, parent, false))
            }
        }

        override fun getItemViewType(position: Int): Int {
            val item=listA[position]
            if(item.isHeader)
            return TYPE_HEADER
            else
                return TYPE_ITEM
        }
        override fun getItemCount(): Int= listA.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is HolderHeader){
                holder.bind(listA[position])
            }else if(holder is HolderAbout){
                val index = listA[position].about?.id
                lateinit var copy : String
                if(index!! >3)
                    copy = listA[position].about?.web.toString()
                else
                    copy = listA[position].about?.name.toString()
                holder.bind(listA[position], View.OnClickListener { setClipboard(this@AboutActivity,copy) }
               )
                holder.itemView.setOnClickListener {
                    if(index!! >3)
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(listA[position].about?.web!!)))
                    else
                        startNewActivity(this@AboutActivity, listA[position].about?.web!!)
                }
            }
        }
    }

    class HolderAbout(view: View):RecyclerView.ViewHolder(view){
            val imageView = view.findViewById<ImageView>(R.id.imageLogo)
            val judul = view.findViewById<TextView>(R.id.textJudul)
            val detail = view.findViewById<TextView>(R.id.textDetail)
            val imgCopy = view.findViewById<ImageView>(R.id.imageCopy)
        fun bind(about: ListAbout, copy :View.OnClickListener){
            imageView.setImageResource(Fungsi.img_about!![about.about?.id!!])
            judul.text = about.about?.name!!
            detail.text = about.about?.detail!!
            imgCopy.setOnClickListener(copy)
        }
    }
    class HolderHeader(view: View):RecyclerView.ViewHolder(view){
        val textHeader = view.findViewById<TextView>(R.id.text_header)
        fun bind(about: ListAbout){
            textHeader.text = about.titleHeader
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}