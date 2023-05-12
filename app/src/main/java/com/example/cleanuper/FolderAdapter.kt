package com.example.cleanuper

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cleanuper.task.finished.FinishedTask
import com.example.cleanuper.task.finished.FinishedTaskAdapter
import com.example.cleanuper.task.finished.FinishedTaskDetailActivity
import com.example.cleanuper.task.running.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FolderAdapter(private val context: Context, private val progressBar: ProgressBar) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    private val folderNames = listOf("Текущие задачи", "Выполненные задачи")
    private val runningTasks = ArrayList<Task>()
    private val finishedTasks = ArrayList<FinishedTask>()

    init {
        loadTasks()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folderName = folderNames[position]
        holder.bind(folderName)
    }

    override fun getItemCount(): Int {
        return folderNames.size
    }

    fun getFolderName(position: Int): String {
        return folderNames[position]
    }

    private fun loadTasks() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        uid?.let {
            loadRunningTasks(uid)
            loadFinishedTasks(uid)
        }
    }

    private fun loadFinishedTasks(uid: String?) {
        val userTasksRef = FirebaseDatabase.getInstance().getReference("Users/$uid/finishedTasks")
        userTasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                finishedTasks.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(FinishedTask::class.java)
                    task?.let {
                        finishedTasks.add(it)
                    }
                }
                notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadRunningTasks(uid : String?) {
        val userTasksRef = FirebaseDatabase.getInstance().getReference("Users/$uid/tasks")
        userTasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                runningTasks.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let {
                        runningTasks.add(it)
                    }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_tasks)
        private lateinit var taskAdapter: TaskAdapter
        private lateinit var finishedTaskAdapter: FinishedTaskAdapter

        fun bind(folderName: String) {
            if (folderName == folderNames[0]) {
                recyclerView.layoutManager = LinearLayoutManager(context)
                taskAdapter =
                    TaskAdapter(runningTasks) { task, title, description, uid, duration, progress, lastComplete, completes ->
                        val intent = Intent(context, TaskDetailActivity::class.java)
                        intent.putExtra("title", title)
                        intent.putExtra("description", description)
                        intent.putExtra("uid", uid)
                        intent.putExtra("duration", duration.toString())
                        intent.putExtra("progress", progress.toString())
                        intent.putExtra("lastComplete", lastComplete.toString())
                        intent.putExtra("completes", completes)
                        context.startActivity(intent)
                    }
                recyclerView.adapter = taskAdapter
            } else {
                recyclerView.layoutManager = LinearLayoutManager(context)
                finishedTaskAdapter =
                    FinishedTaskAdapter(finishedTasks) { task, title, description, uid, duration, completes ->
                        val intent = Intent(context, FinishedTaskDetailActivity::class.java)
                        intent.putExtra("title", title)
                        intent.putExtra("description", description)
                        intent.putExtra("uid", uid)
                        intent.putExtra("duration", duration.toString())
                        intent.putExtra("completes", completes)
                        context.startActivity(intent)
                    }
                recyclerView.adapter = finishedTaskAdapter
            }
        }
    }
}

