package com.messenger.indiChat.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.Adapter.UserAdapter
import com.messenger.indiChat.R
import com.messenger.indiChat.models.User
import com.messenger.indiChat.ChatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()

    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Get current user ID from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getString("userId", null)

        if (currentUserId == null) {
            // User not logged in, redirect to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        recyclerUsers = findViewById(R.id.recyclerUsers)
        recyclerUsers.layoutManager = LinearLayoutManager(this)

        // Dummy data (replace with actual DB / API later)
        userList.add(User("1", "Deepti", "Hey, how are you?", "10:30"))
        userList.add(User("2", "Tushar", "See you soon", "11:15"))
        userList.add(User("3", "ABC", "Okay thanks!", "12:45"))

        userAdapter = UserAdapter(userList) { selectedUser ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", selectedUser.id)       // Receiver ID
            intent.putExtra("userName", selectedUser.name)   // Receiver Name
            intent.putExtra("currentUserId", currentUserId)  // Logged-in user
            startActivity(intent)
        }

        recyclerUsers.adapter = userAdapter
    }
}
