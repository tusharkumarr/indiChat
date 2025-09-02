package com.messenger.indiChat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.R

class ReelsAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<ReelsAdapter.ReelViewHolder>() {

    class ReelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reelImage: ImageView = itemView.findViewById(R.id.reelImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reel, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holder.reelImage.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size
}
