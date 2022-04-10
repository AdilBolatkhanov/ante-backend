package com.adil.data.requests

data class EditGoalRequest(
    val title: String,
    val dueDate: Long,
    val tag: String,
    val backgroundColor: String,
    val iconName: String,
    val isPrivate: Boolean,
    val subGoal: List<EditSubGoalRequest>,
    val id: String
)

data class EditSubGoalRequest(
    val title: String,
    val dueDate: Long,
    val isCompleted: Boolean
)