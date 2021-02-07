package com.futuradev.githubber.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearSnapHelper
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.CustomItemUrls
import com.futuradev.githubber.ui.repository.details.CustomRecyclerAdapter
import com.futuradev.githubber.utils.listeners.CustomRecyclerItemListener
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

        adapter.listener = object : CustomRecyclerItemListener {
            override fun onThumbnailClicked(url: String?) {
                openInBrowser(context, url)
            }
        }
        LinearSnapHelper().apply {
            attachToRecyclerView(recycler.apply {
                onFlingListener = null
            })
        }
    }

    private fun openInBrowser(context: Context, url: String?) {
        url?.let {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            context.startActivity(browserIntent)
        }
    }

    fun setData(title: String? = null, data: List<CustomItemUrls>) {
        title?.let { this.title.text = it }
        items_count.text = data.size.toString()
        adapter.setData(data)
    }

}