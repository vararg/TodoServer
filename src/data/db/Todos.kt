package com.vararg.data.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Todos: Table() {
    val todoId : Column<Long> = long("id").autoIncrement()
    val userId : Column<Long> = long("userId").references(Users.userId)
    val content = varchar("todo", 512)
    val isDone = bool("isDone")

    override val primaryKey = PrimaryKey(todoId, name = "PKUId")
}
