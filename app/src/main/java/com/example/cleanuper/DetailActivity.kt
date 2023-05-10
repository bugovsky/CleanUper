package com.example.cleanuper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.example.cleanuper.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
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
        val taskId = intent.getStringExtra("uid").toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        binding.deleteButton.setOnClickListener {
            uid?.let {
                val taskRef =
                    FirebaseDatabase.getInstance().getReference("Users/$uid/tasks").child(taskId)

                taskRef.removeValue()
                    .addOnSuccessListener {
                        startActivity(Intent(this@DetailActivity, MainActivity::class.java))
                        Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(
                            this,
                            "Ошибка при удалении задачи: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}