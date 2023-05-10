package com.example.cleanuper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cleanuper.authentication.LoginActivity
import com.example.cleanuper.databinding.ActivityMainBinding
import com.example.cleanuper.task.Task
import com.example.cleanuper.task.TaskAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var adapter: TaskAdapter
    private val taskList: ArrayList<Task> = ArrayList()

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

        binding.recyclerTasks.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(taskList) { task, title, description, uid ->
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("description", description)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }
        binding.recyclerTasks.adapter = adapter

        binding.addTask.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddActivity::class.java))
        }

        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid
        uid?.let {
            val userTasksRef = FirebaseDatabase.getInstance().getReference("Users/$uid/tasks")
            userTasksRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    taskList.clear()
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        task?.let {
                            taskList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()

                    binding.progressBar.visibility = View.GONE
                    binding.recyclerTasks.visibility = View.VISIBLE
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}