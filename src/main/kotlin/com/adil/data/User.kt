package com.adil.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val login: String,
    val password: String,
    val fistName: String,
    val lastName: String,
    val image: String,
    @BsonId
    val id: String = ObjectId().toString()
)