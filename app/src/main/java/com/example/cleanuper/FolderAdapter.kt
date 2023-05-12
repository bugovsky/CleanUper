package com.example.cleanuper

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cleanuper.task.running.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FolderAdapter(private val context: Context, private val progressBar: ProgressBar) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    private val folderNames = listOf("Текущие задачи", "Выполненные задачи")
    private val taskLists: MutableList<ArrayList<Task>> = mutableListOf()

    init {
        for (i in folderNames.indices) {
            taskLists.add(ArrayList())
        }
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
            val userTasksRef = FirebaseDatabase.getInstance().getReference("Users/$uid/tasks")
            userTasksRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    taskLists[0].clear()
                    taskLists[1].clear()
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        task?.let {
                            val isCompleted = task.progress == task.duration
                            val taskListIndex = if (isCompleted) 1 else 0
                            taskLists[taskListIndex].add(it)
                        }
                    }
                    notifyDataSetChanged()

                    // Hide the progress bar
                    progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_tasks)
        private lateinit var adapter: TaskAdapter

        fun bind(folderName: String) {
            val taskListIndex = if (folderName == "Выполненные задачи") 1 else 0
            val tasks = taskLists[taskListIndex]

            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = TaskAdapter(tasks) { task, title, description, uid, duration, progress, lastComplete ->
                val intent = Intent(context, TaskDetailActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("description", description)
                intent.putExtra("uid", uid)
                intent.putExtra("duration", duration.toString())
                intent.putExtra("progress", progress.toString())
                intent.putExtra("lastComplete", lastComplete.toString())
                context.startActivity(intent)
            }
            recyclerView.adapter = adapter
        }
    }
}

