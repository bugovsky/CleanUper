package com.example.cleanuper.task.finished

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cleanuper.R

class FinishedTaskAdapter(
    private val tasks: ArrayList<FinishedTask>,
    private val onItemClick: (FinishedTask, String, String, String, Int, ArrayList<Long>) -> Unit
) : RecyclerView.Adapter<FinishedTaskAdapter.FinishedTaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishedTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.finished_task_item, parent, false)
        return FinishedTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinishedTaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
        holder.itemView.setOnClickListener {
            onItemClick(
                task,
                task.title,
                task.description,
                task.taskId,
                task.duration,
                task.completes
            )
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class FinishedTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: FinishedTask) {
            val title = itemView.findViewById<TextView>(R.id.title)
            val description = itemView.findViewById<TextView>(R.id.description)

            title.text = task.title
            description.text = task.description
        }
    }
}