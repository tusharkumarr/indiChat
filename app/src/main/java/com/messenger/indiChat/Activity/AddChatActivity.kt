package com.messenger.indiChat.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.Adapter.UserAdapter
import com.messenger.indiChat.ChatActivity
import com.messenger.indiChat.R
import com.messenger.indiChat.models.User
import com.messenger.indiChat.network.RetrofitClient
import com.messenger.indiChat.repository.ChatRepository
import kotlinx.coroutines.launch

class AddChatActivity : AppCompatActivity() {

    private lateinit var recyclerNewUsers: RecyclerView
    private lateinit var searchNewUser: SearchView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()

    private lateinit var chatRepository: ChatRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chat)

        recyclerNewUsers = findViewById(R.id.recyclerNewUsers)
        searchNewUser = findViewById(R.id.searchNewUser)

        recyclerNewUsers.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList) { user ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", user.phoneNumber)
            intent.putExtra("userName", user.name)
            startActivity(intent)
            finish()
        }
        recyclerNewUsers.adapter = userAdapter

        chatRepository = ChatRepository(RetrofitClient.chatApi(this))

        // 🔹 Only trigger search when input length >= 2
        searchNewUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty() && newText.length >= 2) {
                    searchUsers(newText)
                } else {
                    userList.clear()
                    userAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    private fun searchUsers(query: String) {
        lifecycleScope.launch {
            try {
                val results = chatRepository.searchUsers(query)
                userList.clear()
                userList.addAll(results)
                userAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
