package com.vararg.data.models

import io.ktor.auth.Principal
import java.io.Serializable

data class User(
    val userId: Long,
    val email: String,
    val userName: String,
    val passwordHash: String
) : Serializable, Principal