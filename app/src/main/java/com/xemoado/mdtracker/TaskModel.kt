package com.xemoado.mdtracker

data class TaskModel(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false
)