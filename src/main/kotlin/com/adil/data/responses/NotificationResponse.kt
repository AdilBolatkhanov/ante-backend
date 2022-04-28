package com.adil.data.responses

import com.adil.data.collections.PostType

data class NotificationResponse(
    val followingEvent: List<FollowingNotification>,
    val likedNotification: List<LikedNotification>
)

data class FollowingNotification(
    val followerId: String,
    val username: String,
    val isFollowed: Boolean,
    val date: Long,
    val profileImageUrl: String?
)

data class LikedNotification(
    val userLikedId: String,
    val dateOfCreation: Long,
    val userLikedUsername: String,
    val userLikedImageUrl: String?,
    val myProfileImage: String?,
    val myUsername: String,
    val myName: String,
    val postId: String,
    val postDate: Long,
    val postDescription: String,
    val postImage: String? = null,
    val postType: PostType,
    val postAchievementId: String? = null,
    val postIconName: String? = null,
    val postBackgroundColor: String? = null,
)
