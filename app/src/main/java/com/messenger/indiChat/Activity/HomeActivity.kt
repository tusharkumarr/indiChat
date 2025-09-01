package com.messenger.indiChat.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.Adapter.UserAdapter
import com.messenger.indiChat.R
import com.messenger.indiChat.models.User
import com.messenger.indiChat.network.RetrofitClient
import com.messenger.indiChat.ChatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()
    private lateinit var progressBar: ProgressBar
    private lateinit var fabNewChat: FloatingActionButton

    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("jwtToken", null)
        currentUserId = sharedPref.getString("userId", "") ?: ""

        if (token.isNullOrEmpty() || currentUserId.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        recyclerUsers = findViewById(R.id.recyclerUsers)
        progressBar = findViewById(R.id.progressBar)
        fabNewChat = findViewById(R.id.fabNewChat)

        recyclerUsers.layoutManager = LinearLayoutManager(this)

        userAdapter = UserAdapter(userList) { selectedUser ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", selectedUser.id)
            intent.putExtra("userName", selectedUser.name)
            startActivity(intent)
        }
        recyclerUsers.adapter = userAdapter

        // Floating Action Button opens AddChatActivity
        fabNewChat.setOnClickListener {
            startActivity(Intent(this, AddChatActivity::class.java))
        }
    }

    // 🔹 Refresh users list every time activity comes to foreground
    override fun onResume() {
        super.onResume()
        fetchChatUsers()
    }

    private fun fetchChatUsers() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val users = RetrofitClient.chatApi(this@HomeActivity).getChatUsers()
                userList.clear()
                userList.addAll(users)
                userAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@HomeActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
