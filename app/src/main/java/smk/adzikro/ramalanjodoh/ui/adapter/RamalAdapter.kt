package smk.adzikro.ramalanjodoh.ui.adapter

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.databinding.ItemRamalBinding
import smk.adzikro.ramalanjodoh.utils.IS_GOOD
import smk.adzikro.ramalanjodoh.utils.OFFLINE_NONFAVORITE
import smk.adzikro.ramalanjodoh.utils.ONLINE_NONFAVORITE
import smk.adzikro.ramalanjodoh.utils.imgBad
import smk.adzikro.ramalanjodoh.utils.imgGood

class RamalAdapter(
    private val onItemClickCallback: OnItemClickCallback
) : PagingDataAdapter<Ramal, RamalAdapter.ViewHolder>(DIFF_CALLBACK)  {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ramal>() {
            override fun areItemsTheSame(oldItem: Ramal, newItem: Ramal): Boolean {
                return  oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Ramal, newItem: Ramal): Boolean {
                return oldItem.status == newItem.status
            }

        }
    }

    inner class ViewHolder(private val v : ItemRamalBinding) : RecyclerView.ViewHolder(v.root){
        fun bind(item : Ramal) {
            item.let {
                v.apply {
                    val options = RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(CenterCrop(), RoundedCorners(10))
                    val img = when(item.ilustratsi){
                        !in 0..49 ->{
                            imgGood[imgGood.indices.random()]
                        }
                        in 0..59 ->
                            if(item.result== IS_GOOD) imgGood[item.ilustratsi] else imgBad[item.ilustratsi]
                        else -> imgBad[imgBad.indices.random()]
                    }

                    Glide.with(itemView.context)
                        .load(img)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(options)
                        .into(itemPhoto)
                    itemDescription.text = item.desc
                    itemDate.text = item.date
                    tvItemName.text = item.pria + " \n" + item.wanita
                    if (item.status == ONLINE_NONFAVORITE || item.status == OFFLINE_NONFAVORITE) {
                        itemFavorite.setColorFilter(ContextCompat.getColor(v.root.context, R.color.linkColor), PorterDuff.Mode.SRC_IN)
                        itemFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp)
                    } else {
                        itemFavorite.setColorFilter(ContextCompat.getColor(v.root.context, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
                        itemFavorite.setImageResource(R.drawable.ic_favorite_white_24dp)
                    }
                    itemPublish.visibility = if(item.status > 1) View.VISIBLE else View.GONE
                    itemDelete.setOnClickListener {
                        onItemClickCallback.onItemDeleteClicked(item)
                    }
                    itemFavorite.setOnClickListener {
                        onItemClickCallback.onItemFavoriteClicked(item)
                    }
                    itemShare.setOnClickListener {
                        onItemClickCallback.onItemShareClicked(v.root)
                    }
                    itemPublish.setOnClickListener {
                        onItemClickCallback.onItemPublishClicked(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRamalBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position) as Ramal)
      //  holder.setIsRecyclable(true)
    }
    interface OnItemClickCallback {
        fun onItemDeleteClicked(data: Ramal)
        fun onItemFavoriteClicked(data: Ramal)
        fun onItemShareClicked(data: View)
        fun onItemPublishClicked(data: Ramal)
    }
}