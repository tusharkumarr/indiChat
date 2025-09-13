package com.messenger.indiChat.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.messenger.indiChat.R
import com.messenger.indiChat.adapters.ReelsAdapter
import com.messenger.indiChat.models.Reel
import com.messenger.indiChat.network.RetrofitClient
import com.messenger.indiChat.repository.ReelRepository
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var reelRepository: ReelRepository
    private lateinit var recyclerReels: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var currentUserId: String = ""
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Profile views
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val profileName = findViewById<TextView>(R.id.profileName)
        val profilePhone = findViewById<TextView>(R.id.profilePhone)

        // Bottom sheet
        val bottomSheet = findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = 300
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        recyclerReels = findViewById(R.id.recyclerReels)
        progressBar = findViewById(R.id.progressBar)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        recyclerReels.layoutManager = GridLayoutManager(this, 3)

        // Load user data
        val sharedPref = getSharedPreferences("indiChatPrefs", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getString("userId", "") ?: ""
        token = sharedPref.getString("jwtToken", null)
        profileName.text = sharedPref.getString("name", "John Doe")
        profilePhone.text = sharedPref.getString("phoneNumber", "9999999999")

        // Init repository
        reelRepository = ReelRepository(RetrofitClient.reelApi(this))

        // Load reels into bottom sheet
        loadReels()
    }

    private fun loadReels() {
        lifecycleScope.launch {
            progressBar.visibility = android.view.View.VISIBLE
            recyclerReels.visibility = android.view.View.GONE

            try {
                val response = reelRepository.getHybridRecommendations(
                    userId = currentUserId,
                    topN = 15
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        val reels = body.data ?: emptyList<Reel>()

                        // ✅ Set adapter with click opening ReelPlayerActivity
                        recyclerReels.adapter = ReelsAdapter(reels) { reel ->
                            val intent = Intent(this@ProfileActivity, ReelPlayerActivity::class.java)
                            intent.putExtra("thumbnailGif", reel.thumbnailGif)
                            intent.putExtra("videoUrl", reel.videoUrl)
                            intent.putExtra("caption", reel.caption)
                            intent.putExtra("id", reel.id)
                            startActivity(intent)
                        }

                        recyclerReels.visibility = android.view.View.VISIBLE
                    } else {
                        // Show message if no reels found
                        android.widget.Toast.makeText(
                            this@ProfileActivity,
                            body?.message ?: "No reels found",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    android.widget.Toast.makeText(
                        this@ProfileActivity,
                        "Failed to fetch reels",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    this@ProfileActivity,
                    "Error: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } finally {
                progressBar.visibility = android.view.View.GONE
            }
        }
    }
}
