package com.example.cleanuper.task

data class Task(var title: String, var description: String) {
    constructor() : this("", "")
}