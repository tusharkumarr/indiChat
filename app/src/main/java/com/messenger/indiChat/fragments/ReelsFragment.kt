package com.messenger.indiChat.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.messenger.indiChat.Activity.AddReelActivity
import com.messenger.indiChat.Activity.LoginActivity
import com.messenger.indiChat.Activity.ReelPlayerActivity
import com.messenger.indiChat.R
import com.messenger.indiChat.adapters.ReelsAdapter
import com.messenger.indiChat.network.RetrofitClient
import kotlinx.coroutines.launch

class ReelsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddReel: FloatingActionButton

    private lateinit var currentUserId: String
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reels, container, false)

        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        token = sharedPref.getString("jwtToken", null)
        currentUserId = sharedPref.getString("userId", "") ?: ""

        // ✅ If no login info found → redirect to LoginActivity
        if (token.isNullOrEmpty() || currentUserId.isEmpty()) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            return view
        }

        recyclerView = view.findViewById(R.id.recyclerUsers)
        progressBar = view.findViewById(R.id.progressBar)
        fabAddReel = view.findViewById(R.id.fabAddReel)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        fabAddReel.setOnClickListener {
            startActivity(Intent(requireContext(), AddReelActivity::class.java))
        }

        loadRecommendedReels()

        return view
    }

    private fun loadRecommendedReels() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            try {
                val api = RetrofitClient.reelApi(requireContext())

                val response = api.getHybridRecommendations(
                    userId = currentUserId,
                    topN = 15
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        val reels = body.data ?: emptyList()
                        recyclerView.adapter = ReelsAdapter(reels) { reel ->
                            val intent = Intent(requireContext(), ReelPlayerActivity::class.java)
                            intent.putExtra("thumbnailGif", reel.thumbnailGif)
                            intent.putExtra("videoUrl", reel.videoUrl)
                            intent.putExtra("caption", reel.caption)
                            intent.putExtra("id", reel.id)
                            startActivity(intent)
                        }
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), body?.message ?: "No recommendations", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecommendedReels()
    }
}
