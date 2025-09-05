package com.messenger.indiChat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import androidx.media3.ui.PlayerView
import com.messenger.indiChat.R

class ReelsPagerAdapter(private val reels: List<String>) :
    RecyclerView.Adapter<ReelsPagerAdapter.ReelViewHolder>() {

    class ReelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        val btnLike: ImageButton = view.findViewById(R.id.btnLike)
        val btnShare: ImageButton = view.findViewById(R.id.btnShare)
        val btnClose: ImageButton = view.findViewById(R.id.btnClose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reel_page, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        // Player setup will be handled in Activity
    }

    override fun getItemCount(): Int = reels.size
}
