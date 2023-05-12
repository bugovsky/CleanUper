package com.example.cleanuper.task.finished

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.example.cleanuper.databinding.ActivityFinishedTaskDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class FinishedTaskDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinishedTaskDetailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var taskRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishedTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.backButton.setOnClickListener {
            onBackPressed()
            finish()
        }
        val intent = intent
        if (intent.hasExtra("title") && intent.hasExtra("description")) {
            val title = intent.getStringExtra("title")
            val description = intent.getStringExtra("description")
            binding.title.text = Editable.Factory.getInstance().newEditable(title)
            binding.description.text = Editable.Factory.getInstance().newEditable(description)
        }
    }
}