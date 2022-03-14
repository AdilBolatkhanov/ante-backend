package com.adil.data.collections

import com.google.gson.annotations.SerializedName
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Goal(
    val title: String,
    val dueDate: Long,
    val tag: GoalTag,
    val backgroundColor: String,
    val iconUrl: String,
    val ownerId: String,
    @BsonId
    val id: String = ObjectId().toString()
)

enum class GoalTag(val title: String){
    @SerializedName("Language")
    LANGUAGE("Language"),
    @SerializedName("Productivity")
    PRODUCTIVITY("Productivity")
}

data class SubGoal(
    val title: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val goalId: String,
    @BsonId
    val id: String = ObjectId().toString()
)