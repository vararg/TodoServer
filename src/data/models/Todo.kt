package com.vararg.data.models

data class Todo(
    val id: Long,
    val userId: Long,
    val content: String,
    val isDone: Boolean
)