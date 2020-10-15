package com.vararg.data

import com.vararg.data.db.DatabaseFactory
import com.vararg.data.db.Todos
import com.vararg.data.models.Todo
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement


class DBTodoRepository : TodoRepository {

    override suspend fun addTodo(userId: Long, content: String, isDone: Boolean): Todo? {
        var statement: InsertStatement<Number>? = null

        DatabaseFactory.dbQuery {
            statement = Todos.insert {
                it[Todos.userId] = userId
                it[Todos.content] = content
                it[Todos.isDone] = isDone
            }
        }

        return statement?.resultedValues?.firstOrNull()?.let { rowToTodo(it) }
    }

    override suspend fun getTodos(userId: Long): List<Todo> = DatabaseFactory.dbQuery {
        Todos.select {
            Todos.userId.eq(userId)
        }.mapNotNull { rowToTodo(it) }
    }

    private fun rowToTodo(row: ResultRow): Todo {
        return Todo(
            id = row[Todos.todoId],
            userId = row[Todos.userId],
            content = row[Todos.content],
            isDone = row[Todos.isDone]
        )
    }
}
