package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class FollowStorage(
    val followerId: String,
    val followingId: String,
    val date: Long,
    @BsonId
    val id: String = ObjectId().toString()
)
