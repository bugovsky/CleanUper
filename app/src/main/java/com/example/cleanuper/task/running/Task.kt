package com.example.cleanuper.task.running

data class Task(
    var title: String,
    var description: String,
    val taskId: String,
    val duration: Int,
    var progress: Int,
    var lastComplete: Long
) {
    constructor() : this("", "", "", 0, 0, 0)
}