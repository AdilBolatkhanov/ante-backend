package com.adil.data.responses

import com.adil.data.collections.GoalTag
import com.adil.data.collections.Habit

data class UserProfile(
    val userId: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Long?,
    val backgroundUrl: String?,
    val profileImageUrl: String?,
    val bio: String?,
    val following: List<String>,
    val followers: List<String>,
    val habits: List<Habit>,
    val goals: List<GoalInProfile>,
    val posts: List<PostInProfile>
)

data class GoalInProfile(
    val title: String,
    val dueDate: Long,
    val tag: GoalTag,
    val backgroundColor: String,
    val iconUrl: String,
    val subGoals: List<Boolean>,
    val ownerId: String,
    val id: String
)

data class PostInProfile(
    val dateOfCreation: Long,
    val authorName: String,
    val authorUsername: String,
    val authorImageUrl: String,
    val likes: Int,
    val comments: Int,
    val description: String,
    val ownerId: String,
    var imageUrl: String? = null,
    val id: String
)