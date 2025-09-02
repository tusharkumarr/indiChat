package com.messenger.indiChat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.R
import com.messenger.indiChat.adapters.ReelsAdapter

class ReelsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reels, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerUsers)

        // Use Grid with 3 columns (like Instagram Reels grid)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Fake image list (replace with real data later)
        val images = listOf(
            R.drawable.ic_person,  // Add some images to your drawable folder
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,  // Add some images to your drawable folder
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,  // Add some images to your drawable folder
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,  // Add some images to your drawable folder
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person,
            R.drawable.ic_person
        )

        recyclerView.adapter = ReelsAdapter(images)

        return view
    }
}
