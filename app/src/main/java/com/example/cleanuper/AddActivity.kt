package com.example.cleanuper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.example.cleanuper.databinding.ActivityAddBinding
import com.example.cleanuper.task.running.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            onBackPressed()
            finish()
        }
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users")
        binding.saveButton.setOnClickListener {
            val title = binding.title.text.toString()
            val description = binding.description.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(this, "Название задачи не может быть пустым!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                uid?.let {
                    val userTasksRef = usersRef.child(it).child("tasks")
                    val taskRef = userTasksRef.push()
                    val uidTask = taskRef.key.toString()
                    val duration = 5 + binding.seekbar.progress * 2
                    val task = Task(title, description, uidTask, duration, 0, 0, ArrayList())
                    taskRef.setValue(task)
                }
                Toast.makeText(this, "Задача сохранена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val text = "Длительность выполнения (дни): ${5 + progress * 2}"
                binding.counter.text = text
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }
}