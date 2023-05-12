package com.example.cleanuper.task.running

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.example.cleanuper.MainActivity
import com.example.cleanuper.databinding.ActivityTaskDetailBinding
import com.example.cleanuper.task.finished.FinishedTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import java.util.concurrent.TimeUnit

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var taskRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
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

        taskRef = FirebaseDatabase.getInstance().getReference("Users/$uid/tasks")
            .child(taskId)

        binding.doneButton.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            uid?.let {
                taskRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val task = snapshot.getValue(Task::class.java)
                        if (task != null) {
                            val lastCompleteTime = task.lastComplete
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = currentTime
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            val nextMidnightTime = calendar.timeInMillis
                            if (lastCompleteTime < nextMidnightTime) {
                                task.lastComplete = currentTime
                                task.completes.add(currentTime)
                                ++task.progress
                                if (task.progress == task.duration) {
                                    addFinishedTask(uid, task)
                                    deleteTask()
                                    Toast.makeText(
                                        this@TaskDetailActivity,
                                        "Задача завершена! Вы можете найти ее в разделе выполненных задач.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    taskRef.setValue(task)
                                        .addOnCompleteListener { taskUpdate ->
                                            if (taskUpdate.isSuccessful) {
                                                startActivity(
                                                    Intent(
                                                        this@TaskDetailActivity,
                                                        MainActivity::class.java
                                                    )
                                                )
                                                Toast.makeText(
                                                    this@TaskDetailActivity,
                                                    "Задача выполнена!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                finish()
                                            } else {
                                                Toast.makeText(
                                                    this@TaskDetailActivity,
                                                    "Не удалось сохранить выполнение...",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            } else {
                                val remainingTime =
                                    nextMidnightTime - currentTime + TimeUnit.DAYS.toMillis(1)
                                val remainingHours = TimeUnit.MILLISECONDS.toHours(remainingTime)
                                val remainingMinutes =
                                    TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60
                                Toast.makeText(
                                    this@TaskDetailActivity,
                                    "Выполнить задачу снова можно через $remainingHours часов и $remainingMinutes минут",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@TaskDetailActivity,
                                "Задача не найдена",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@TaskDetailActivity, error.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        }

        binding.deleteButton.setOnClickListener {
            deleteTask()
        }

        binding.saveButton.setOnClickListener {
            uid?.let {
                val newTitle = binding.title.text
                val newDescription = binding.description.text
                if (newTitle.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "Название задачи не может быть пустым",
                        Toast.LENGTH_SHORT
                    )
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
                                                    this@TaskDetailActivity,
                                                    MainActivity::class.java
                                                )
                                            )
                                            Toast.makeText(
                                                this@TaskDetailActivity,
                                                "Задача обновлена",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@TaskDetailActivity,
                                                "Не удалось обновить задачу",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this@TaskDetailActivity,
                                    "Задача не найдена",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@TaskDetailActivity,
                                error.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
                }
            }
        }
    }

    private fun addFinishedTask(uid: String?, task: Task) {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        uid?.let {
            val userTasksRef = usersRef.child(it).child("finishedTasks")
            val taskRef = userTasksRef.push()
            val uidTask = taskRef.key.toString()
            val finishedTask =
                FinishedTask(task.title, task.description, uidTask, task.duration, task.completes)
            taskRef.setValue(finishedTask)
        }
    }

    private fun deleteTask() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            taskRef.removeValue()
                .addOnSuccessListener {
                    startActivity(Intent(this@TaskDetailActivity, MainActivity::class.java))
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