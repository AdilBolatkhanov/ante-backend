package com.adil.data.requests

data class EditHabitRequest(
    val title: String,
    val backgroundColor: String,
    val iconName: String,
    val tag: String,
    val targetNumOfDays: Int,
    val isPrivate: Boolean,
    val id: String
)
