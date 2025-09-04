package com.messenger.indiChat.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.messenger.indiChat.R

class ReelPlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnLike: ImageButton
    private lateinit var btnShare: ImageButton
    private lateinit var btnClose: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reel_player)

        videoView = findViewById(R.id.videoView)
        btnLike = findViewById(R.id.btnLike)
        btnShare = findViewById(R.id.btnShare)
        btnClose = findViewById(R.id.btnClose)

        val reelUrl = intent.getStringExtra("videoUrl") ?: ""
        if (reelUrl.isNotEmpty()) {
            setupVideoPlayer(reelUrl)
        }

        btnLike.setOnClickListener {
            Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show()
            // TODO: call API to like reel
        }

        btnShare.setOnClickListener {
            // Share via intent
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

    private fun setupVideoPlayer(url: String) {
        val uri = Uri.parse(url)
        videoView.setVideoURI(uri)

        // Optional: add media controls (play/pause)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true  // loop the video
            videoView.start()
        }

        videoView.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Failed to play video", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }
}
