package com.futuradev.githubber.ui.repository.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.Organization
import kotlinx.android.synthetic.main.organization_item.view.*

class OrganizationsAdapter : RecyclerView.Adapter<OrganizationsAdapter.ViewHolder>() {

    private var context : Context? = null
    private var organizations = mutableListOf<Organization>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.organization_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val current = organizations[position]

        holder.apply {

            context?.let {
                Glide
                    .with(it)
                    .load(current.avatar_url)
                    .circleCrop()
                    .into(organizationThumbnail)
            }
        }
    }

    override fun getItemCount(): Int = organizations.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val organizationThumbnail = itemView.organization_thumbnail
    }

    fun setData(list : List<Organization>)  {

        organizations.clear()
        organizations.addAll(list)
        notifyDataSetChanged()
    }

}