package com.vararg.routes

import com.vararg.auth.AuthSession
import com.vararg.auth.JwtService
import com.vararg.data.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*


const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
fun Route.users(
    userRepository: UserRepository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UserCreateRoute> {
        val signupParameters = call.receiveParameters()
        val password = signupParameters["password"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        val displayName = signupParameters["userName"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        val email = signupParameters["email"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        val hash = hashFunction(password)

        try {
            val newUser = userRepository.add(email, displayName, hash)
            newUser!!.userId.let {
                call.sessions.set(AuthSession(it))
                call.respondText(jwtService.generateToken(newUser), status = HttpStatusCode.Created)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

    post<UserLoginRoute> {
        val loginParams = call.receiveParameters()

        val email = loginParams["email"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        val password = loginParams["password"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        val hash = hashFunction(password)

        try {
            val currentUser = userRepository.findByEmail(email)
            currentUser!!.userId.let {
                if (currentUser.passwordHash == hash) {
                    call.sessions.set(AuthSession(it))
                    call.respond(jwtService.generateToken(currentUser))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                }
            }

        } catch (e: Throwable) {
            application.log.error("Failed to retrieve user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
        }
    }
}
