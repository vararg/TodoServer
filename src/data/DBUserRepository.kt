package com.vararg.data

import com.vararg.data.db.DatabaseFactory
import com.vararg.data.db.Users
import com.vararg.data.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class DBUserRepository : UserRepository {

    override suspend fun add(email: String, userName: String, passwordHash: String): User? {
        var statement: InsertStatement<Number>? = null

        DatabaseFactory.dbQuery {
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.userName] = userName
                user[Users.passwordHash] = passwordHash
            }
        }

        return statement?.resultedValues?.firstOrNull()?.let { rowToUser(it) }
    }

    override suspend fun findById(userId: Long): User? = DatabaseFactory.dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    override suspend fun findByEmail(email: String): User? = DatabaseFactory.dbQuery {
        Users.select { Users.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            userId = row[Users.userId],
            email = row[Users.email],
            userName = row[Users.userName],
            passwordHash = row[Users.passwordHash]
        )
    }
}
