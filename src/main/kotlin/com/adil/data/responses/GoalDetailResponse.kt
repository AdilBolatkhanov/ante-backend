package com.adil.data.responses

data class GoalDetailResponse(
    val title: String,
    val dueDate: Long,
    val tag: String,
    val backgroundColor: String,
    val iconName: String,
    val ownerId: String,
    val isPrivate: Boolean,
    val id: String,
    val subGoals: List<SubGoalDetailResponse>
)
data class SubGoalDetailResponse(
    val title: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val goalId: String,
    val id: String
)
