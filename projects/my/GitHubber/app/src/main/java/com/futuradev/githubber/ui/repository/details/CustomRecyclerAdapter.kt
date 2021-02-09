package com.futuradev.githubber.ui.repository.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.ImageItemUrls
import com.futuradev.githubber.utils.listeners.ImageRecyclerItemListener
import kotlinx.android.synthetic.main.image_recycler_item.view.*

class CustomRecyclerAdapter : RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    private var context : Context? = null
    private var data = mutableListOf<ImageItemUrls>()

    var listener : ImageRecyclerItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_recycler_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val current = data[position]

        holder.apply {
            context?.let {
                Glide
                    .with(it)
                    .load(current.thumbnailUrl)
                    .circleCrop()
                    .into(thumbnail)
            }

            thumbnail.setOnClickListener { listener?.onThumbnailClicked(current.browserUrl) }
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail = itemView.thumbnail
    }

    fun setData(list : List<ImageItemUrls>)  {

        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

}