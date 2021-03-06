package com.adil.routing

import com.adil.API_VERSION
import com.adil.auth.JwtService
import com.adil.data.*
import com.adil.data.requests.AccountLoginRequest
import com.adil.data.requests.RegisterUserRequest
import com.adil.data.requests.toUser
import com.adil.data.responses.UserCredentialsResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"
const val USER_DELETE = "$USERS/delete"

fun Application.registerUserRoutes(jwtService: JwtService) {
    routing {
        createUserRoute(jwtService)
        loginRoute(jwtService)
        deleteUserRoute()
    }
}

fun Route.createUserRoute(jwt: JwtService) {
    post(USER_CREATE) {
        val account = try {
            call.receive<RegisterUserRequest>()
        } catch (e: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val userExists = findUserByEmail(account.email) != null
        if (!userExists) {
            val user = account.toUser()
            if (registerUser(user))
                call.respond(HttpStatusCode.OK, UserCredentialsResponse(jwt.generateToken(user), user.id))
            else
                call.respond(HttpStatusCode.BadRequest, "An unknown error occurred!")
        } else {
            call.respond(HttpStatusCode.BadRequest, "A user with such email already exists!")
        }
    }
}

fun Route.deleteUserRoute() {
    authenticate {
        delete(USER_DELETE) {
            val id = call.principal<UserIdPrincipal>()!!.name
            if (deleteUser(id))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }
    }
}

fun Route.loginRoute(jwt: JwtService) {
    post(USER_LOGIN) {
        val request = try {
            call.receive<AccountLoginRequest>()
        } catch (e: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val isPasswordCorrect = checkPasswordForEmail(request.email, request.password)
        if (isPasswordCorrect) {
            val user = findUserByEmail(request.email) ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "The user with such email does not exist"
            )
            call.respond(
                HttpStatusCode.OK,
                UserCredentialsResponse(jwt.generateToken(user), user.id)
            )
        } else
            call.respond(HttpStatusCode.BadRequest, "The password or email is incorrect")
    }
}