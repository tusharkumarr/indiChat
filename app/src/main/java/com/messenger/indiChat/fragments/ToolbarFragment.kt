package com.messenger.indiChat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.messenger.indiChat.R

class ToolbarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_toolbar, container, false)

        val logoutIcon = view.findViewById<ImageView>(R.id.logoutIcon)
        logoutIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Logout clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
