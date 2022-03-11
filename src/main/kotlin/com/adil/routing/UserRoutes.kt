package com.adil.routing

import com.adil.API_VERSION
import com.adil.auth.JwtService
import com.adil.data.checkPasswordForEmail
import com.adil.data.findUser
import com.adil.data.registerUser
import com.adil.data.requests.AccountLoginRequest
import com.adil.data.requests.RegisterUserRequest
import com.adil.data.requests.toUser
import com.adil.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"
const val USER_DELETE = "$USERS/delete"

fun Application.registerUserRoute(jwtService: JwtService) {
    routing {
        createUser(jwtService)
        loginRoute(jwtService)
    }
}

fun Route.createUser(jwt: JwtService) {
    post(USER_CREATE) {
        val account = try {
            call.receive<RegisterUserRequest>()
        } catch (e: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val userExists = findUser(account.email) != null
        if (!userExists) {
            val user = account.toUser()
            if (registerUser(user))
                call.respond(HttpStatusCode.OK, SimpleResponse(true, jwt.generateToken(user)))
            else
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "An unknown error occured!"))
        } else {
            call.respond(HttpStatusCode.OK, SimpleResponse(false, "A user with such email already exists!"))
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
        if (isPasswordCorrect)
            call.respond(HttpStatusCode.OK, SimpleResponse(true, jwt.generateToken(findUser(request.email)!!)))
        else
            call.respond(HttpStatusCode.OK, SimpleResponse(false, "The password or email is incorrect"))
    }
}