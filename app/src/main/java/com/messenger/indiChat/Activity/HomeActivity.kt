package com.messenger.indiChat.Activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.messenger.indiChat.Adapter.ViewPagerAdapter
import com.messenger.indiChat.R
import com.messenger.indiChat.fragments.ChatsFragment
import com.messenger.indiChat.fragments.ReelsFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var menuButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home) // make sure activity_home uses custom toolbar layout

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        menuButton = findViewById(R.id.menuButton)

        // Setup ViewPager and Tabs
        val fragments = listOf(ChatsFragment(), ReelsFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Chats" else "Reels"
        }.attach()

        tabLayout.setSelectedTabIndicatorColor(getColor(R.color.purple_500))
        tabLayout.setTabTextColors(getColor(R.color.gray), getColor(R.color.white))
        tabLayout.setBackgroundColor(getColor(R.color.purple_500))

        // Custom menu popup
        menuButton.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.top_app_bar_menu, popup.menu)
            popup.gravity = Gravity.END
            popup.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.action_myprofile -> {
                        Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show(); true
                    }
                    R.id.action_logout -> {
                        val sharedPref = getSharedPreferences("indiChatPrefs", MODE_PRIVATE)
                        sharedPref.edit().clear().apply()

                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        finish()
                        true
                    }
                    R.id.action_settings -> {
                        Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show(); true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}
