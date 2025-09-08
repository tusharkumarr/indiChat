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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.messenger.indiChat.Activity.AddChatActivity
import com.messenger.indiChat.Activity.LoginActivity
import com.messenger.indiChat.Adapter.UserAdapter
import com.messenger.indiChat.ChatActivity
import com.messenger.indiChat.R
import com.messenger.indiChat.models.User
import com.messenger.indiChat.network.RetrofitClient
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()
    private lateinit var progressBar: ProgressBar
    private lateinit var fabNewChat: FloatingActionButton

    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("jwtToken", null)
        currentUserId = sharedPref.getString("userId", "") ?: ""

        if (token.isNullOrEmpty() || currentUserId.isEmpty()) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            return view
        }

        recyclerUsers = view.findViewById(R.id.recyclerUsers)
        progressBar = view.findViewById(R.id.progressBar)
        fabNewChat = view.findViewById(R.id.fabNewChat)

        recyclerUsers.layoutManager = LinearLayoutManager(requireContext())

        userAdapter = UserAdapter(userList) { selectedUser ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("userId", selectedUser.id)
            intent.putExtra("userName", selectedUser.name)
            startActivity(intent)
        }
        recyclerUsers.adapter = userAdapter

        fabNewChat.setOnClickListener {
            startActivity(Intent(requireContext(), AddChatActivity::class.java))
        }

        fetchChatUsers()
        return view
    }

    override fun onResume() {
        super.onResume()
        fetchChatUsers()
    }

    private fun fetchChatUsers() {
        progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.chatApi(requireContext()).getChatUsers()

                if (response.success) {  // assuming GenericResponse has a 'success' field
                    userList.clear()
                    userList.addAll(response.data ?: emptyList()) // ✅ use response.data
                    userAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), response.message ?: "Failed to load users", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
