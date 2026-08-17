package smk.adzikro.ramalanjodoh.ui.adapter

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.util.Log
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
import smk.adzikro.ramalanjodoh.data.models.Ramalx
import smk.adzikro.ramalanjodoh.databinding.ItemRamalxBinding
import smk.adzikro.ramalanjodoh.utils.IS_GOOD
import smk.adzikro.ramalanjodoh.utils.config
import smk.adzikro.ramalanjodoh.utils.dateToString
import smk.adzikro.ramalanjodoh.utils.imgBad
import smk.adzikro.ramalanjodoh.utils.imgGood

class RamalxAdapter(
    private val onItemClickCallback: OnItemClickCallback
) : PagingDataAdapter<Ramalx, RamalxAdapter.ViewHolder>(DIFF_CALLBACK)  {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ramalx>() {
            override fun areItemsTheSame(oldItem: Ramalx, newItem: Ramalx): Boolean {
                return oldItem.ramalid == newItem.ramalid
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Ramalx, newItem: Ramalx): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val v : ItemRamalxBinding) : RecyclerView.ViewHolder(v.root){
        fun bind(item : Ramalx) {
            item.let {
                v.apply {
                    val options = RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(CenterCrop(), RoundedCorners(10))
                    // 💡 AMANKAN INDEKS DI SINI
                    val maxIndex = if (item.result == IS_GOOD) imgGood.size else imgBad.size
                    val img = if (item.img in 0 until maxIndex) {
                        if (item.result == IS_GOOD) imgGood[item.img] else imgBad[item.img]
                    } else {
                        imgGood[imgGood.indices.random()]
                    }
                    Glide.with(itemView.context)
                        .load(img)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(options)
                        .into(itemPhoto)
                    Log.e("Ramalx", item.displayName!!)
                    itemDescription.text = item.desc
                    itemDate.text = dateToString(item.date)
                    itemUser.text = item.displayName
                    tvItemName.text = item.pria + " \n" + item.wanita
                    if (item.favorite.size < 1) {
                        itemFavorite.setColorFilter(ContextCompat.getColor(v.root.context, R.color.linkColor), PorterDuff.Mode.SRC_IN)
                        itemFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp)
                        tvItemFavoriteCount.text = v.root.context.getString(R.string.suka_count, item.favorite.size)
                    } else {
                        itemFavorite.setColorFilter(ContextCompat.getColor(v.root.context, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
                        itemFavorite.setImageResource(R.drawable.ic_favorite_white_24dp)
                        tvItemFavoriteCount.text = v.root.context.getString(R.string.suka_count, item.favorite.size)
                    }
                    if(item.uid == v.root.context.config.userUid){
                        itemSave.visibility = View.GONE
                    }else{
                        itemSave.visibility = View.VISIBLE
                    }
                    tvItemCommentCount.text = v.root.context.getString(R.string.comment_count, item.message)

                    itemComment.setOnClickListener {
                        onItemClickCallback.onItemCommentClicked(item)
                    }
                    itemFavorite.setOnClickListener {
                        onItemClickCallback.onItemFavoriteClicked(item)
                    }
                    itemShare.setOnClickListener {
                        onItemClickCallback.onItemShareClicked(v.root)
                    }
                    itemSave.setOnClickListener {
                        onItemClickCallback.onItemSaveClicked(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRamalxBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position) as Ramalx)
        holder.setIsRecyclable(true)
    }
    interface OnItemClickCallback {
        fun onItemCommentClicked(data: Ramalx)
        fun onItemFavoriteClicked(data: Ramalx)
        fun onItemShareClicked(data: View)
        fun onItemSaveClicked(data: Ramalx)
    }
}