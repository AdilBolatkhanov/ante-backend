package com.adil.routing

import com.adil.API_VERSION
import com.adil.data.*
import com.adil.data.collections.PostType
import com.adil.data.responses.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

const val NOTIFICATION = "$API_VERSION/notification"

fun Application.registerNotificationRoutes() {
    routing {
        authenticate {
            notificationRoutes()
        }
    }
}

fun Route.notificationRoutes() {
    route(NOTIFICATION) {
        get {
            val myId = call.principal<UserIdPrincipal>()!!.name
            val myProfile =
                findUser(myId) ?: return@get call.respond(HttpStatusCode.BadRequest, "No user with such id exists")
            val followingNotification = getFollowedEvent(myId).map { followStorage ->
                val followerInfo = findUser(followStorage.followerId)
                FollowingNotification(
                    followerId = followStorage.followerId,
                    isFollowed = myProfile.following.contains(followStorage.followerId),
                    date = followStorage.date,
                    profileImageUrl = followerInfo?.profileImageUrl,
                    username = followerInfo?.username.orEmpty()
                )
            }
            val likedRegularPostNotifications = mutableListOf<LikedRegularPostNotification>()
            val likedAchievementPostNotifications = mutableListOf<LikedAchievementPostNotification>()
            getLikeEvent(myId).map { like ->
                val userInfo = findUser(like.userLikedId)
                getPostById(like.postId)?.let { post ->
                    when (post.type) {
                        PostType.GOAL_ACHIEVEMENT -> {
                            val goal = getGoalById(post.achievementId.orEmpty())
                            goal?.let { curGoal ->
                                likedAchievementPostNotifications.add(
                                    LikedAchievementPostNotification(
                                        dateOfCreation = like.date,
                                        postDescription = curGoal.title,
                                        postType = post.type,
                                        postAchievementId = post.achievementId,
                                        postIconName = curGoal.iconName,
                                        postBackgroundColor = curGoal.backgroundColor,
                                        userLikedId = like.userLikedId,
                                        userLikedUsername = userInfo?.username.orEmpty(),
                                        userLikedImageUrl = userInfo?.profileImageUrl.orEmpty(),
                                        myName = "${myProfile.firstName} ${myProfile.lastName}",
                                        myProfileImage = myProfile.profileImageUrl,
                                        myUsername = myProfile.username,
                                        postId = like.postId,
                                        postDate = post.dateOfCreation,
                                    )
                                )
                            }
                        }
                        PostType.HABIT_ACHIEVEMENT -> {
                            val habit = getHabitById(post.achievementId.orEmpty())
                            habit?.let { curHabit ->
                                likedAchievementPostNotifications.add(
                                    LikedAchievementPostNotification(
                                        dateOfCreation = like.date,
                                        postDescription = curHabit.title,
                                        postType = post.type,
                                        postAchievementId = post.achievementId,
                                        postIconName = curHabit.iconName,
                                        postBackgroundColor = curHabit.backgroundColor,
                                        userLikedId = like.userLikedId,
                                        userLikedUsername = userInfo?.username.orEmpty(),
                                        userLikedImageUrl = userInfo?.profileImageUrl.orEmpty(),
                                        myName = "${myProfile.firstName} ${myProfile.lastName}",
                                        myProfileImage = myProfile.profileImageUrl,
                                        myUsername = myProfile.username,
                                        postId = like.postId,
                                        postDate = post.dateOfCreation,
                                    )
                                )
                            }
                        }
                        else -> {
                            likedRegularPostNotifications.add(
                                LikedRegularPostNotification(
                                    dateOfCreation = like.date,
                                    postDescription = post.description,
                                    postType = post.type,
                                    userLikedId = like.userLikedId,
                                    userLikedUsername = userInfo?.username.orEmpty(),
                                    userLikedImageUrl = userInfo?.profileImageUrl.orEmpty(),
                                    myName = "${myProfile.firstName} ${myProfile.lastName}",
                                    myProfileImage = myProfile.profileImageUrl,
                                    myUsername = myProfile.username,
                                    postId = like.postId,
                                    postDate = post.dateOfCreation,
                                    postImage = post.imageUrl
                                )
                            )
                        }
                    }
                }
            }
            val commentsOfRegularPostNotifications = mutableListOf<CommentRegularPostNotification>()
            val commentsOfAchievementPostNotifications = mutableListOf<CommentAchievementPostNotification>()
            getCommentEvent(myId).map { commentStorage ->
                val userInfo = findUser(commentStorage.writerId)
                getPostById(commentStorage.postId)?.let { post ->
                    when (post.type) {
                        PostType.GOAL_ACHIEVEMENT -> {
                            val goal = getGoalById(post.achievementId.orEmpty())
                            goal?.let { curGoal ->
                                commentsOfAchievementPostNotifications.add(
                                    CommentAchievementPostNotification(
                                        dateOfComment = commentStorage.date,
                                        postDescription = curGoal.title,
                                        postType = post.type,
                                        postAchievementId = post.achievementId,
                                        postIconName = curGoal.iconName,
                                        postBackgroundColor = curGoal.backgroundColor,
                                        commenterId = commentStorage.writerId,
                                        commenterUsername = userInfo?.username.orEmpty(),
                                        commenterImageUrl = userInfo?.profileImageUrl.orEmpty(),
                                        myName = "${myProfile.firstName} ${myProfile.lastName}",
                                        myProfileImage = myProfile.profileImageUrl,
                                        myUsername = myProfile.username,
                                        postId = commentStorage.postId,
                                        postDate = post.dateOfCreation,
                                        comment = commentStorage.commentText
                                    )
                                )
                            }
                        }
                        PostType.HABIT_ACHIEVEMENT -> {
                            val habit = getHabitById(post.achievementId.orEmpty())
                            habit?.let { curHabit ->
                                commentsOfAchievementPostNotifications.add(
                                    CommentAchievementPostNotification(
                                        dateOfComment = commentStorage.date,
                                        postDescription = curHabit.title,
                                        postType = post.type,
                                        postAchievementId = post.achievementId,
                                        postIconName = curHabit.iconName,
                                        postBackgroundColor = curHabit.backgroundColor,
                                        commenterId = commentStorage.writerId,
                                        commenterUsername = userInfo?.username.orEmpty(),
                                        commenterImageUrl = userInfo?.profileImageUrl.orEmpty(),
                                        myName = "${myProfile.firstName} ${myProfile.lastName}",
                                        myProfileImage = myProfile.profileImageUrl,
                                        myUsername = myProfile.username,
                                        postId = commentStorage.postId,
                                        postDate = post.dateOfCreation,
                                        comment = commentStorage.commentText
                                    )
                                )
                            }
                        }
                        else -> {
                            commentsOfRegularPostNotifications.add(
                                CommentRegularPostNotification(
                                    dateOfComment = commentStorage.date,
                                    postDescription = post.description,
                                    postType = post.type,
                                    commenterId = commentStorage.writerId,
                                    commenterUsername = userInfo?.username.orEmpty(),
                                    commenterImageUrl = userInfo?.profileImageUrl.orEmpty(),
                                    myName = "${myProfile.firstName} ${myProfile.lastName}",
                                    myProfileImage = myProfile.profileImageUrl,
                                    myUsername = myProfile.username,
                                    postId = commentStorage.postId,
                                    postDate = post.dateOfCreation,
                                    postImage = post.imageUrl,
                                    comment = commentStorage.commentText
                                )
                            )
                        }
                    }
                }
            }
            call.respond(
                HttpStatusCode.OK, NotificationResponse(
                    followingEvents = followingNotification,
                    likesOfRegularPostNotifications = likedRegularPostNotifications,
                    likesOfAchievementNotifications = likedAchievementPostNotifications,
                    commentsOfRegularPostNotifications, commentsOfAchievementPostNotifications
                )
            )
        }
    }
}