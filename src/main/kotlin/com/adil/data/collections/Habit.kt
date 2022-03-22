package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Habit(
    val title: String,
    val backgroundColor: String,
    val iconName: String,
    val tag: String,
    val startDate: Long,
    val targetNumOfDays: Int,
    val curNumOfDays: Int,
    val ownerId: String,
    @BsonId
    val id: String = ObjectId().toString()
)

