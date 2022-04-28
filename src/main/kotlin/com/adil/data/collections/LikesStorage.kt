package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class LikesStorage(
    val userLikedId: String,
    val postId: String,
    val postOwnerId: String,
    val date: Long,
    @BsonId
    val id: String = ObjectId().toString()
)
