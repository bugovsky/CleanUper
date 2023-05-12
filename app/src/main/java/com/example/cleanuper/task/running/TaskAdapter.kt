package com.example.cleanuper.task.running

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cleanuper.R

class TaskAdapter(
    private val tasks: ArrayList<Task>,
    private val onItemClick: (Task, String, String, String, Int, Int, Long, ArrayList<Long>) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
        holder.itemView.setOnClickListener {
            onItemClick(
                task,
                task.title,
                task.description,
                task.taskId,
                task.duration,
                task.progress,
                task.lastComplete,
                task.completes
            )
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            val title = itemView.findViewById<TextView>(R.id.title)
            val description = itemView.findViewById<TextView>(R.id.description)
            val progressBar: ProgressBar = itemView.findViewById(R.id.day_counter_bar)

            title.text = task.title
            description.text = task.description
            progressBar.max = task.duration
            progressBar.progress = task.progress
        }
    }
}

