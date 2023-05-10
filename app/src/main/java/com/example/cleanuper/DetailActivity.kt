package com.example.cleanuper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.example.cleanuper.databinding.ActivityDetailBinding
import com.example.cleanuper.task.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        binding.saveButton.setOnClickListener {
            uid?.let {
                val taskRef =
                    FirebaseDatabase.getInstance().getReference("Users/$uid/tasks").child(taskId)
                val newTitle = binding.title.text;
                val newDescription = binding.description.text;
                if (newTitle.isNullOrEmpty()) {
                    Toast.makeText(this, "Название задачи не может быть пустым", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    taskRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val task = snapshot.getValue(Task::class.java)
                            if (task != null) {
                                task.title = newTitle.toString()
                                task.description = newDescription.toString()
                                taskRef.setValue(task)
                                    .addOnCompleteListener { taskUpdate ->
                                        if (taskUpdate.isSuccessful) {
                                            startActivity(
                                                Intent(
                                                    this@DetailActivity,
                                                    MainActivity::class.java
                                                )
                                            )
                                            Toast.makeText(
                                                this@DetailActivity,
                                                "Задача обновлена",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@DetailActivity,
                                                "Не удалось обновить задачу",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this@DetailActivity,
                                    "Задача не найдена",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@DetailActivity, error.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                }
            }
        }
    }
}