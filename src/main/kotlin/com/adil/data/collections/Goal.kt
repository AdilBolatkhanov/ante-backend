package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Goal(
    val title: String,
    val dueDate: Long,
    val tag: String,
    val backgroundColor: String,
    val iconName: String,
    val ownerId: String,
    @BsonId
    val id: String = ObjectId().toString()
)

data class SubGoal(
    val title: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val goalId: String,
    @BsonId
    val id: String = ObjectId().toString()
)