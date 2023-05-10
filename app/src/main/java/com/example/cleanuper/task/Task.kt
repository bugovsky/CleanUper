package com.example.cleanuper.task

data class Task(var title: String, var description: String, val taskId: String) {
    constructor() : this("", "", "")
}