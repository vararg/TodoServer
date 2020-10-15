package com.vararg.data

import com.vararg.data.models.User

interface UserRepository {
    suspend fun add(email: String, userName: String, passwordHash: String): User?

    suspend fun findById(userId: Long): User?

    suspend fun findByEmail(email: String): User?
}
