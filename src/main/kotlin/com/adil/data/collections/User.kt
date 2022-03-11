package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val dateOfBirth: Long,
    @BsonId
    val id: String = ObjectId().toString()
)