package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post(
    val dateOfCreation: Long,
    val peopleLiked: List<String>,
    val description: String,
    val ownerId: String,
    val imageUrl: String? = null,
    @BsonId
    val id: String = ObjectId().toString()
)

data class Comment(
    val dateOfCreation: Long,
    val text: String,
    val peopleLiked: List<String>,
    val authorName: String,
    val authorUsername: String,
    val authorImageUrl: String,
    val authorId: String,
    val postId: String,
    val peopleHelpful: List<String>,
    @BsonId
    val id: String = ObjectId().toString()
)