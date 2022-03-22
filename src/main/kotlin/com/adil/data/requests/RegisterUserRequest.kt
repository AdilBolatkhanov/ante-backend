package com.adil.data.requests

import com.adil.data.collections.User
import com.adil.security.hashPassword

data class RegisterUserRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

fun RegisterUserRequest.toUser() = User(
    username, hashPassword(password), firstName, lastName, email
)
