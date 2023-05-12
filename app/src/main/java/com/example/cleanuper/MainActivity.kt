package com.example.cleanuper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.example.cleanuper.authentication.LoginActivity
import com.example.cleanuper.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var folderAdapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        folderAdapter = FolderAdapter(this, binding.progressBar)
        binding.viewPager.adapter = folderAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = folderAdapter.getFolderName(position)
        }.attach()
        binding.tabLayout.tabMode = TabLayout.MODE_FIXED
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedTabName = folderAdapter.getFolderName(tab.position)
                if (selectedTabName == "Выполненные задачи") {
                    binding.addTask.visibility = View.GONE
                } else {
                    binding.addTask.visibility = View.VISIBLE
                }

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val tabWidth = screenWidth / 2

        val tabStrip = binding.tabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            val tabView = tabStrip.getChildAt(i)
            val layoutParams = tabView.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = tabWidth
            tabView.layoutParams = layoutParams
        }

        binding.addTask.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddActivity::class.java))
        }
    }
}