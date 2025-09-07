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
    private lateinit var adapter: ReelsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reel_player)

        viewPager = findViewById(R.id.reelViewPager)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        // Get initial URL from previous activity
        intent.getStringExtra("videoUrl")?.let {
            reelUrls.add(it)
        }

        adapter = ReelsPagerAdapter(reelUrls)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                playVideoAt(position)
                checkAndLoadMore(position)
            }
        })
    }

    private fun playVideoAt(position: Int) {
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
    }

    private fun checkAndLoadMore(position: Int) {
        if (position == reelUrls.size - 1) {
            // Post the adapter update to avoid RecyclerView layout crash
            val newUrls = getNextReelUrls()
            if (newUrls.isNotEmpty()) {
                reelUrls.addAll(newUrls)
                viewPager.post {
                    adapter.notifyItemRangeInserted(position + 1, newUrls.size)
                }
            }
        }
    }

    private fun getNextReelUrls(): List<String> {
        // Replace these URLs with your API fetched URLs
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
