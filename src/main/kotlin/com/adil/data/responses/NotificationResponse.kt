package com.adil.data.responses

import com.adil.data.collections.PostType

data class NotificationResponse(
    val followingEvents: List<FollowingNotification>,
    val likesOfRegularPostNotifications: List<LikedRegularPostNotification>,
    val likesOfAchievementNotifications: List<LikedAchievementPostNotification>,
    val commentsOfRegularPostNotifications: List<CommentRegularPostNotification>,
    val commentsOfAchievementPostNotifications: List<CommentAchievementPostNotification>
)

data class FollowingNotification(
    val followerId: String,
    val username: String,
    val isFollowed: Boolean,
    val date: Long,
    val profileImageUrl: String?
)

data class CommentRegularPostNotification(
    val comment: String,
    val postId: String,
    val commenterId: String,
    val dateOfComment: Long,
    val commenterUsername: String,
    val commenterImageUrl: String,
    val myProfileImage: String?,
    val myUsername: String,
    val myName: String,
    val postDate: Long,
    val postDescription: String,
    val postImage: String? = null,
    val postType: PostType
)

data class CommentAchievementPostNotification(
    val comment: String,
    val postId: String,
    val commenterId: String,
    val dateOfComment: Long,
    val commenterUsername: String,
    val commenterImageUrl: String,
    val myProfileImage: String?,
    val myUsername: String,
    val myName: String,
    val postDate: Long,
    val postDescription: String,
    val postType: PostType,
    val postAchievementId: String? = null,
    val postIconName: String? = null,
    val postBackgroundColor: String? = null,
)

data class LikedRegularPostNotification(
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
    val postType: PostType
)

data class LikedAchievementPostNotification(
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
    val postType: PostType,
    val postAchievementId: String? = null,
    val postIconName: String? = null,
    val postBackgroundColor: String? = null,
)
