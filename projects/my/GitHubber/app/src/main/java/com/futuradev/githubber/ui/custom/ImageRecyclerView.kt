package com.futuradev.githubber.ui.custom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearSnapHelper
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.ImageItemUrls
import com.futuradev.githubber.ui.repository.details.CustomRecyclerAdapter
import com.futuradev.githubber.utils.listeners.BrowserListener
import com.futuradev.githubber.utils.listeners.ImageRecyclerItemListener
import kotlinx.android.synthetic.main.image_recycler_view.view.*
import org.koin.core.KoinComponent

class ImageRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle), KoinComponent {

    val adapter = CustomRecyclerAdapter()

    init {
        inflate(context, R.layout.image_recycler_view, this)

        recycler.adapter = adapter

        adapter.listener = object : ImageRecyclerItemListener {
            override fun onThumbnailClicked(url: String?) {
                url?.let {
                    (context as? BrowserListener)?.openInBrowser(it)
                }
            }
        }
        LinearSnapHelper().apply {
            attachToRecyclerView(recycler.apply {
                onFlingListener = null
            })
        }
    }

    fun setData(title: String? = null, data: List<ImageItemUrls>) {
        title?.let { this.title.text = it }
        items_count.text = data.size.toString()
        adapter.setData(data)
    }

}