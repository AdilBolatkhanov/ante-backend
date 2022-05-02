package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class CommentStorage(
    val commentText: String,
    val postId: String,
    val writerId: String,
    val date: Long,
    val ownerOfPostId: String,
    @BsonId
    val id: String = ObjectId().toString()
)
