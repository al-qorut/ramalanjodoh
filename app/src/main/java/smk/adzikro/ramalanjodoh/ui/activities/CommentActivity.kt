package smk.adzikro.ramalanjodoh.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Comment
import smk.adzikro.ramalanjodoh.data.models.Ramalx
import smk.adzikro.ramalanjodoh.databinding.ActivityCommentBinding
import smk.adzikro.ramalanjodoh.ui.adapter.CommentAdapter
import smk.adzikro.ramalanjodoh.utils.IS_GOOD
import smk.adzikro.ramalanjodoh.utils.dateToString
import smk.adzikro.ramalanjodoh.utils.imgBad
import smk.adzikro.ramalanjodoh.utils.imgGood
import smk.adzikro.ramalanjodoh.viewmodels.CommentViewModel

@AndroidEntryPoint
class CommentActivity: BaseActivity(){

    private var _binding: ActivityCommentBinding? = null
    private val binding get() = _binding!!
    val viewModelx by viewModels<CommentViewModel>()
    private lateinit var adapterComment: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            setSupportActionBar(commentToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = getString(R.string.comment)
        }
        val data = intent.getParcelableExtra<Ramalx>(DETAIL)

        v = binding.adViewContainer
        data?:return
        showData(data)
    }

    private fun showData(item:Ramalx){
        val options = FirestoreRecyclerOptions.Builder<Comment>()
            .setQuery(viewModelx.getQueryMessage(item.ramalid), Comment::class.java)
            .build()
        adapterComment = CommentAdapter(options)
        //Log.e("Comment", "showData: ${adapterComment.itemCount}")

        binding.apply {
            val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transform(CenterCrop(), RoundedCorners(10))
            val maxIndex = if (item.result == IS_GOOD) imgGood.size else imgBad.size

            val img = if (item.img in 0 until maxIndex) {
                if (item.result == IS_GOOD) imgGood[item.img] else imgBad[item.img]
            } else {
                imgGood[imgGood.indices.random()]
            }
            Glide.with(this@CommentActivity)
                .load(img)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(itemPhoto)
            itemDescription.text = item.desc
            itemDate.text = dateToString(item.date)
            itemUser.text = item.displayName
            tvItemName.text = item.pria + " \n" + item.wanita
            listItemComment.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@CommentActivity)
                adapter = adapterComment
            }
            /*viewModelx.getQueryMessage(item.ramalid).addSnapshotListener { value, error ->
                if (error != null) {
                    // Tangani kesalahan jika ada
                    Log.e("FirestoreError", "Error fetching data: ${error.message}")
                    return@addSnapshotListener
                }

                if(value!=null){
                    if(value.isEmpty){
                        binding.viewEmpties.root.visibility = View.VISIBLE
                        binding.listComment.visibility = View.GONE
                        binding.viewEmpties.tvNoData.text = getString(R.string.comment_kosong)
                    }else{
                        binding.listComment.visibility = View.VISIBLE
                        binding.viewEmpties.root.visibility = View.GONE
                    }
                }
            } */

            viewModelx.isLoading.observe(this@CommentActivity){
                progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
            simpanComment.setOnClickListener {
                if(commentInput.text.toString().isEmpty()) return@setOnClickListener
                viewModelx.addComment(item.ramalid, commentInput.text.toString())
                commentInput.setText("")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapterComment.startListening()

    }
    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        adapterComment.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapterComment.stopListening()
        _binding = null
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DETAIL = "detail"
    }
}