package com.adil.data

import com.adil.security.hashPassword
import com.adil.data.collections.User
import com.adil.utils.Constants.ANTE_BACKEND
import com.adil.utils.Constants.MONGODB_URI
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient(System.getenv(MONGODB_URI)).coroutine
private val database = client.getDatabase(ANTE_BACKEND)
private val users = database.getCollection<User>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return hashPassword(passwordToCheck) == actualPassword
}

suspend fun findUser(email: String): User? {
    return users.findOne(User::email eq email)
}