package com.vararg.data.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {

    val userId: Column<Long> = long("id").autoIncrement()
    val email = varchar("email", 128).uniqueIndex()
    val userName = varchar("user_name", 256)
    val passwordHash = varchar("password_hash", 64)

    override val primaryKey = PrimaryKey(userId, name = "PKId")
}
