package com.example.connect

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchButton: ImageButton

    private val chatFragment = ChatFragment()
    private val profileFragment = ProfileFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        searchButton = findViewById(R.id.main_search_btn)


        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.menu_chat) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, chatFragment).commit()
            }
            if (item.itemId == R.id.menu_profile) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, profileFragment).commit()
            }
            true
        }
        bottomNavigationView.selectedItemId = R.id.menu_chat


        searchButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchUserActivity::class.java))
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}