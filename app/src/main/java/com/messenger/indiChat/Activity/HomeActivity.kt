package com.messenger.indiChat.Activity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerUsers = findViewById(R.id.recyclerUsers)
        recyclerUsers.layoutManager = LinearLayoutManager(this)

        // Dummy data (replace with actual DB / API later)
        userList.add(User("1", "Alice", "Hey, how are you?", "10:30"))
        userList.add(User("2", "Bob", "See you soon", "11:15"))
        userList.add(User("3", "Charlie", "Okay thanks!", "12:45"))

        userAdapter = UserAdapter(userList) { selectedUser ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", selectedUser.id)
            intent.putExtra("userName", selectedUser.name)
            startActivity(intent)
        }

        recyclerUsers.adapter = userAdapter
    }
}
