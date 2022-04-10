package com.adil.data.requests

data class AddGoalRequest(
    val title: String,
    val dueDate: Long,
    val tag: String,
    val backgroundColor: String,
    val iconName: String,
    val isPrivate: Boolean,
    val subGoal: List<AddSubGoalRequest>,
)

data class AddSubGoalRequest(
    val title: String,
    val dueDate: Long,
    val isCompleted: Boolean
)