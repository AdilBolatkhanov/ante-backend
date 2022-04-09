package com.adil.data.responses

data class HabitResponse(
    val title: String,
    val backgroundColor: String,
    val iconName: String,
    val tag: String,
    val startDate: Long,
    val targetNumOfDays: Int,
    val curNumOfDays: Int,
    val ownerId: String,
    val canMarkToday: Boolean,
    val isPrivate: Boolean,
    val id: String
)
