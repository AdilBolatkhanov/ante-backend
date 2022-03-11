package com.adil.data.requests

import com.adil.data.collections.User

data class RegisterUserRequest(
    val username: String,
    val password: String,
    val fistName: String,
    val lastName: String,
    val email: String,
    val dateOfBirth: Long
)

fun RegisterUserRequest.toUser() = User(
    username, password, fistName, lastName, email, dateOfBirth
)
