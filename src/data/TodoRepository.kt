package com.vararg.data

import com.vararg.data.models.Todo

interface TodoRepository {

    suspend fun addTodo(userId: Long, content: String, isDone: Boolean): Todo?

    suspend fun getTodos(userId: Long): List<Todo>
}
