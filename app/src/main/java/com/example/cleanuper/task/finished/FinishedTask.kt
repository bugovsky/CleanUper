package com.example.cleanuper.task.finished

data class FinishedTask(
    var title: String,
    var description: String,
    val taskId: String,
    val duration: Int,
    var completes: ArrayList<Long>
) {
    constructor() : this("", "", "", 0, ArrayList())
}