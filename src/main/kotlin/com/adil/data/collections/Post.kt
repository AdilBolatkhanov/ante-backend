package com.adil.data.collections

import com.google.gson.annotations.SerializedName
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post(
    val dateOfCreation: Long,
    val peopleLiked: List<String> = emptyList(),
    val description: String,
    val ownerId: String,
    val imageUrl: String? = null,
    val achievementId: String? = null,
    val type: PostType,
    @BsonId
    val id: String = ObjectId().toString()
)

enum class PostType(val value: String){
    @SerializedName("Regular")
    REGULAR("Regular"),
    @SerializedName("Goal_Achievement")
    GOAL_ACHIEVEMENT("Goal_Achievement"),
    @SerializedName("Habit_Achievement")
    HABIT_ACHIEVEMENT("Habit_Achievement")
}

data class Comment(
    val dateOfCreation: Long,
    val text: String,
    val peopleLiked: List<String> = emptyList(),
    val authorId: String,
    val postId: String,
    val peopleHelpful: List<String> = emptyList(),
    @BsonId
    val id: String = ObjectId().toString()
)