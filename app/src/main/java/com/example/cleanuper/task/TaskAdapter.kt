package com.example.cleanuper.task

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cleanuper.R

class TaskAdapter(private val tasks: ArrayList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            // Привязка данных к элементам CardView
            val titleTextView = itemView.findViewById<TextView>(R.id.title)
            val descTextView = itemView.findViewById<TextView>(R.id.description)

            titleTextView.text = task.title
            descTextView.text = task.description
        }
    }
}
