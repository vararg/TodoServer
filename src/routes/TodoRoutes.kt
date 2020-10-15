package com.vararg.routes

import com.vararg.auth.AuthSession
import com.vararg.data.TodoRepository
import com.vararg.data.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val TODOS = "$API_VERSION/todos"

@KtorExperimentalLocationsAPI
@Location(TODOS)
class TodoRoute

@KtorExperimentalLocationsAPI
fun Route.todos(todoRepository: TodoRepository, userRepository: UserRepository) {
    authenticate("jwt") {
        post<TodoRoute> {
            val todosParams = call.receiveParameters()
            val content = todosParams["content"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing Content")
            val isDone = todosParams["isDone"].toBoolean()

            val user = call.sessions.get<AuthSession>()?.let {
                userRepository.findById(it.userId)
            }

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@post
            }

            try {
                val currentTodo = todoRepository.addTodo(userId = user.userId, content = content, isDone = isDone)
                call.respond(HttpStatusCode.OK, currentTodo!!)
            } catch (e: Throwable) {
                application.log.error("Failed to add todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Saving Todo")
            }
        }

        get<TodoRoute> {
            val user = call.sessions.get<AuthSession>()?.let {
                userRepository.findById(it.userId)
            }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }

            try {
                val todos = todoRepository.getTodos(user.userId)
                call.respond(todos)
            } catch (e: Throwable) {
                application.log.error("Failed to get Todos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Todos")
            }
        }
    }
}
