package com.vararg

import com.vararg.auth.AuthSession
import com.vararg.auth.JwtService
import com.vararg.auth.hash
import com.vararg.data.DBTodoRepository
import com.vararg.data.DBUserRepository
import com.vararg.data.db.DatabaseFactory
import com.vararg.routes.todos
import com.vararg.routes.users
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(Sessions) {
        cookie<AuthSession>("AUTH_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    DatabaseFactory.init()
    val userRepository = DBUserRepository()
    val todoRepository = DBTodoRepository()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Test_Server_1"

            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asLong()
                return@validate userRepository.findById(claimString)
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        users(userRepository, jwtService, hashFunction)
        todos(todoRepository, userRepository)
    }
}
