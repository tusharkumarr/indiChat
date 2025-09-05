package com.messenger.indiChat.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.messenger.indiChat.R

class ReelPlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null
    private lateinit var btnLike: ImageButton
    private lateinit var btnShare: ImageButton
    private lateinit var btnClose: ImageButton
    private var reelUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reel_player)

        playerView = findViewById(R.id.playerView)
        btnLike = findViewById(R.id.btnLike)
        btnShare = findViewById(R.id.btnShare)
        btnClose = findViewById(R.id.btnClose)

        reelUrl = intent.getStringExtra("videoUrl") ?: ""
        if (reelUrl.isNotEmpty()) {
            setupExoPlayer(reelUrl)
        }

        btnLike.setOnClickListener {
            Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show()
            // TODO: call API to like reel
        }

        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, reelUrl)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Reel via"))
        }

        btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupExoPlayer(url: String) {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE // loop video
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
