package com.messenger.indiChat.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.messenger.indiChat.R
import com.messenger.indiChat.adapters.ReelsPagerAdapter

class ReelPlayerActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private var currentPlayer: ExoPlayer? = null
    private val reelUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reel_pager)

        viewPager = findViewById(R.id.reelViewPager)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        // Get initial URL from previous activity
        val initialUrl = intent.getStringExtra("videoUrl")
        if (initialUrl != null) {
            reelUrls.add(initialUrl)
        }

        val adapter = ReelsPagerAdapter(reelUrls)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Release previous player
                currentPlayer?.release()

                val recycler = viewPager.getChildAt(0) as RecyclerView
                val holder =
                    recycler.findViewHolderForAdapterPosition(position) as? ReelsPagerAdapter.ReelViewHolder

                holder?.let { vh ->
                    val player = ExoPlayer.Builder(this@ReelPlayerActivity).build()
                    vh.playerView.player = player

                    val mediaItem = MediaItem.fromUri(Uri.parse(reelUrls[position]))
                    player.setMediaItem(mediaItem)
                    player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                    player.prepare()
                    player.play()

                    currentPlayer = player

                    // Buttons logic
                    vh.btnLike.setOnClickListener {
                        Toast.makeText(this@ReelPlayerActivity, "Liked!", Toast.LENGTH_SHORT).show()
                    }
                    vh.btnShare.setOnClickListener {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, reelUrls[position])
                        }
                        startActivity(Intent.createChooser(shareIntent, "Share Reel via"))
                    }
                    vh.btnClose.setOnClickListener {
                        finish()
                    }
                }

                // Load more URLs dynamically when reaching last page
                if (position == reelUrls.size - 1) {
                    val newUrls = getNextReelUrls()
                    if (newUrls.isNotEmpty()) {
                        reelUrls.addAll(newUrls)
                        adapter.notifyItemRangeInserted(position + 1, newUrls.size)
                    }
                }
            }
        })
    }

    private fun getNextReelUrls(): List<String> {
        return listOf(
            "https://www.w3schools.com/html/mov_bbb.mp4",
            "https://www.w3schools.com/html/mov_bbb.mp4"
        )
    }

    override fun onPause() {
        super.onPause()
        currentPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        currentPlayer?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPlayer?.release()
    }
}
