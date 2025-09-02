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
import kotlinx.coroutines.launch

class AddChatActivity : AppCompatActivity() {

    private lateinit var recyclerNewUsers: RecyclerView
    private lateinit var searchNewUser: SearchView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()
    private val allUsers = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chat)

        recyclerNewUsers = findViewById(R.id.recyclerNewUsers)
        searchNewUser = findViewById(R.id.searchNewUser)

        searchNewUser.setIconifiedByDefault(false)
        searchNewUser.isIconified = false
        searchNewUser.clearFocus()

        searchNewUser.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                searchNewUser.isIconified = false
            }
        }


        recyclerNewUsers.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList) { user ->
            // Open ChatActivity with selected user
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", user.phoneNumber)
            intent.putExtra("userName", user.name)
            startActivity(intent)
            finish()
        }
        recyclerNewUsers.adapter = userAdapter

        fetchAllUsers()

        // SearchView listener
        searchNewUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterUsers(newText)
                return true
            }
        })
    }

    private fun fetchAllUsers() {
        lifecycleScope.launch {
            try {
                val users = RetrofitClient.chatApi(this@AddChatActivity).getAllUsers()
                allUsers.clear()
                allUsers.addAll(users)
                userList.clear()
                userList.addAll(users)
                userAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterUsers(query: String?) {
        val filtered = if (query.isNullOrEmpty()) allUsers else allUsers.filter {
            it.phoneNumber.contains(query)  // <-- matches the User model
        }
        userList.clear()
        userList.addAll(filtered)
        userAdapter.notifyDataSetChanged()
    }
}
