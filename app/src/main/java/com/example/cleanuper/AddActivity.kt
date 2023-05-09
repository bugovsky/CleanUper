package com.example.cleanuper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.cleanuper.databinding.ActivityAddBinding
import com.example.cleanuper.task.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users")
        binding.saveButton.setOnClickListener {
            val title = binding.title.text.toString()
            val description = binding.description.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(this, "Название задачи не может быть пустым!", Toast.LENGTH_SHORT).show()
            } else {
                val task = Task(title, description)
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                uid?.let {
                    val userTasksRef = usersRef.child(it).child("tasks")
                    val taskRef = userTasksRef.push()
                    taskRef.setValue(task)
                }
                Toast.makeText(this, "Задача сохранена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}