package com.adil.data.requests

import com.google.gson.annotations.SerializedName

data class AchievementPostRequest(
    val achievementId: String,
    val type: AchievementPostTypeRequest
)

enum class AchievementPostTypeRequest(val value: String){
    @SerializedName("Goal")
    GOAL("Goal"),
    @SerializedName("Habit")
    HABIT("Habit")
}