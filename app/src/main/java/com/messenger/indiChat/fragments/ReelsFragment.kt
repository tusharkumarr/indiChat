package com.messenger.indiChat.fragments

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
import com.messenger.indiChat.Activity.AddReelActivity
import com.messenger.indiChat.Activity.ReelPlayerActivity
import com.messenger.indiChat.R
import com.messenger.indiChat.adapters.ReelsAdapter
import com.messenger.indiChat.network.RetrofitClient
import kotlinx.coroutines.launch

class ReelsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reels, container, false)

        recyclerView = view.findViewById(R.id.recyclerUsers)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        loadReels()

        // Floating action button to add new reel
        val fabAddReel = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.fabAddReel
        )
        fabAddReel.setOnClickListener {
            startActivity(Intent(requireContext(), AddReelActivity::class.java))
        }

        return view
    }

    private fun loadReels() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            try {
                val response = RetrofitClient.reelApi(requireContext()).getReels()
                if (response.success) {
                    val reels = response.data ?: emptyList()
                    recyclerView.adapter = ReelsAdapter(reels) { reel ->
                        // Open full-screen player when a reel is clicked
                        val intent = Intent(requireContext(), ReelPlayerActivity::class.java)
                        intent.putExtra("thumbnailGif", reel.thumbnailGif)
                        intent.putExtra("videoUrl", reel.videoUrl)
                        intent.putExtra("caption", reel.caption)
                        intent.putExtra("id", reel.id)
                        startActivity(intent)
                    }

                    recyclerView.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), response.message ?: "Failed to load reels", Toast.LENGTH_SHORT).show()
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
        loadReels()
    }
}

