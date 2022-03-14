package com.adil.data.collections

import com.google.gson.annotations.SerializedName
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Habit(
    val title: String,
    val backgroundColor: String,
    val iconUrl: String,
    val tag: HabitTag,
    val startDate: Long,
    val targetNumOfDays: Int,
    val curNumOfDays: Int,
    val ownerId: String,
    @BsonId
    val id: String = ObjectId().toString()
)

enum class HabitTag(val title: String){
    @SerializedName("Good")
    GOOD("Good"),
    @SerializedName("Bad")
    BAD("Bad")
}
