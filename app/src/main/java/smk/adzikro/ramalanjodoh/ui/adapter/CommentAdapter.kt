package smk.adzikro.ramalanjodoh.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Comment
import smk.adzikro.ramalanjodoh.databinding.ItemCommentBinding
import smk.adzikro.ramalanjodoh.utils.config
import smk.adzikro.ramalanjodoh.utils.dateToString

class CommentAdapter(options: FirestoreRecyclerOptions<Comment>) :
    FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder>(options)  {


    inner class ViewHolder(private val v : ItemCommentBinding) : RecyclerView.ViewHolder(v.root){
        fun bind(item : Comment) {
            item.let {
                v.apply {
                    itemComment.text = item.message
                    itemDate.text = dateToString(item.date)
                    itemUser.text = item.displayName
                    if(item.uid == v.root.context.config.userUid){
                        viewComment.background = v.root.context.getDrawable(R.drawable.bg_link_color)
                    }else{
                        viewComment.background = null
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model : Comment) {
        holder.bind(model)
    }
}