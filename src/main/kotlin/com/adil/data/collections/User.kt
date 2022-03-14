package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val dateOfBirth: Long? = null,
    val backgroundUrl: String? = null,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList(),
    @BsonId
    val id: String = ObjectId().toString()
)





