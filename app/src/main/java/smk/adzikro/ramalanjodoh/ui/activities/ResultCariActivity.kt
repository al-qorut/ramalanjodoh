package smk.adzikro.ramalanjodoh.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alqorut.mystory.views.ConfirmationDialog
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramalx
import smk.adzikro.ramalanjodoh.databinding.ActivitySearchBinding
import smk.adzikro.ramalanjodoh.ui.adapter.SearchAdapter
import smk.adzikro.ramalanjodoh.utils.captureViewAsBitmap
import smk.adzikro.ramalanjodoh.utils.shareImage
import smk.adzikro.ramalanjodoh.utils.toRamal

class ResultCariActivity : BaseActivity(), SearchAdapter.OnItemClickCallback {
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            setSupportActionBar(cariToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = getString(R.string.hasil_pencarian)
        }
        val data = intent.extras?.getString(EXTRA_DATA)
        v = binding.adViewContainer
        data?:return
        showData(data)
    }
    private fun showData(data: String){
        val options = FirestoreRecyclerOptions.Builder<Ramalx>()
            .setQuery(viewModel.getQueryCari(data), Ramalx::class.java)
            .build()
        adapter = SearchAdapter(options, this)
        binding.apply {
            listCari.setHasFixedSize(true)
            listCari.layoutManager = LinearLayoutManager(this@ResultCariActivity)
            listCari.adapter = adapter

        }
        viewModel.getQueryCari(data).addSnapshotListener { value, error ->
            if (error != null) {
                // Tangani kesalahan jika ada
                Log.e("FirestoreError", "Error fetching data: ${error.message}")
                return@addSnapshotListener
            }

            if(value!=null){
                if(value.isEmpty){
                    binding.viewEmpties.root.visibility = View.VISIBLE
                    binding.listCari.visibility = View.GONE
                    binding.viewEmpties.tvNoData.text = getString(R.string.data_cari_kosong)
                }else{
                    binding.listCari.visibility = View.VISIBLE
                    binding.viewEmpties.root.visibility = View.GONE
                }
            }
        }


        viewModel.isLoading.observe(this){
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
        _binding = null
    }

    override fun onItemCommentClicked(data: Ramalx) {
        val intent  = Intent(this, CommentActivity::class.java)
        intent.putExtra(CommentActivity.DETAIL, data)
        intent.putExtra("isFromSearch", true)
        startActivity(intent)
    }

    override fun onItemFavoriteClicked(data: Ramalx) {
        viewModel.toggleFavorite(data.ramalid)
    }

    override fun onItemShareClicked(data: View) {
        val bitmap = captureViewAsBitmap(data)
        shareImage(this, bitmap)
    }

    override fun onItemSaveClicked(data: Ramalx) {
        ConfirmationDialog(this, getString(R.string.add_favorite)) {
            viewModel.addRamal(toRamal(data))
        }
    }



    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}