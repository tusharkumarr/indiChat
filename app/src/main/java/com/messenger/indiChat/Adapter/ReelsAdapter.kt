package com.messenger.indiChat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.messenger.indiChat.R
import com.messenger.indiChat.models.Reel

class ReelsAdapter(
    private val reels: List<Reel>,
    private val onReelClick: (Reel) -> Unit = {} // ✅ default no-op
) : RecyclerView.Adapter<ReelsAdapter.ReelViewHolder>() {

    class ReelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reelImage: ImageView = itemView.findViewById(R.id.reelImage)
        val reelCaption: TextView = itemView.findViewById(R.id.reelCaption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reel, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val reel = reels[position]

        // Load video thumbnail or placeholder
        Glide.with(holder.itemView.context)
            .load(reel.thumbnailGif)
            .placeholder(R.drawable.ic_person)
            .into(holder.reelImage)

        holder.reelCaption.text = reel.caption

        // Handle click
        holder.itemView.setOnClickListener {
            onReelClick(reel)
        }
    }

    override fun getItemCount(): Int = reels.size
}
