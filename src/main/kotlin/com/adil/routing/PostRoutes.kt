package com.adil.routing

import com.adil.API_VERSION
import com.adil.data.*
import com.adil.data.collections.PostType
import com.adil.data.requests.*
import com.adil.data.responses.CommentsResponse
import com.adil.data.responses.PostResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val POST = "$API_VERSION/post"

fun Application.registerPostRoutes() {
    routing {
        authenticate {
            postsRoutes()
            commentsRoutes()
        }
    }
}

fun Route.commentsRoutes(){
    route("$POST/comments"){
        get{
            val myId = call.principal<UserIdPrincipal>()!!.name
            val postId = call.request.queryParameters["id"] ?:  return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

            val comments = getCommentsForPost(postId).map { comment ->
                val userInfo = findUser(comment.authorId)
                userInfo?.let { _ ->
                    CommentsResponse(
                        comment.dateOfCreation,
                        "${userInfo.firstName} ${userInfo.lastName}",
                        userInfo.username,
                        userInfo.profileImageUrl,
                        userInfo.id,
                        comment.peopleLiked.size,
                        comment.text,
                        comment.peopleLiked.contains(myId),
                        comment.peopleHelpful.contains(myId),
                        comment.id
                    )
                }
            }.mapNotNull {
                it
            }.sortedByDescending { it.dateOfCreation }

            call.respond(HttpStatusCode.OK, comments)
        }

        post {
            val myId = call.principal<UserIdPrincipal>()!!.name
            val comment = try {
                call.receive<AddCommentRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (addComment(comment.postId, comment.text, myId)){
                getPostById(comment.postId)?.let {
                    addCommentEvent(comment.text, comment.postId, myId, it.ownerId)
                }
                call.respond(HttpStatusCode.OK)
            }else{
                call.respond(HttpStatusCode.Conflict)
            }
        }

        post("/like") {
            val id = call.principal<UserIdPrincipal>()!!.name
            val commentId = try {
                call.receive<IdRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (likeUnlikeComment(commentId.id, id))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        post("/helpful") {
            val id = call.principal<UserIdPrincipal>()!!.name
            val commentId = try {
                call.receive<IdRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (helpfulComment(commentId.id, id))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }
    }
}

fun Route.postsRoutes() {
    route(POST) {
        post("/regular") {
            val id = call.principal<UserIdPrincipal>()!!.name
            val request = try {
                call.receive<RegularPostRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (addRegularPost(request.description, request.imageUrl, id))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        post("/achievement") {
            val id = call.principal<UserIdPrincipal>()!!.name
            val request = try {
                call.receive<AchievementPostRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val res = when (request.type) {
                AchievementPostTypeRequest.GOAL -> addAchievementPost(
                    PostType.GOAL_ACHIEVEMENT,
                    request.achievementId,
                    id
                )
                else -> addAchievementPost(PostType.HABIT_ACHIEVEMENT, request.achievementId, id)
            }
            if (res)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        post("/like") {
            val id = call.principal<UserIdPrincipal>()!!.name
            val postId = try {
                call.receive<IdRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (likeUnlikePost(postId.id, id))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        get {
            val id = call.principal<UserIdPrincipal>()!!.name
            val currentUser =
                findUser(id) ?: return@get call.respond(HttpStatusCode.BadRequest, "No user with such id exists")
            val postsOfUsers = currentUser.following.map { followingId ->
                getPostForUser(followingId).map { post ->
                    val comments = getCommentsForPost(post.id)
                    val userInfo = findUser(post.ownerId)
                    userInfo?.let {
                        when (post.type) {
                            PostType.GOAL_ACHIEVEMENT -> {
                                val goal = getGoalById(post.achievementId.orEmpty())
                                goal?.let { curGoal ->
                                    PostResponse(
                                        dateOfCreation = post.dateOfCreation,
                                        id = post.id,
                                        likes = post.peopleLiked.size,
                                        comments = comments.size,
                                        description = curGoal.title,
                                        authorImageUrl = userInfo.profileImageUrl,
                                        authorName = "${userInfo.firstName} ${userInfo.lastName}",
                                        authorUsername = userInfo.username,
                                        ownerId = post.ownerId,
                                        imageUrl = post.imageUrl,
                                        type = post.type,
                                        achievementId = post.achievementId,
                                        iconName = curGoal.iconName,
                                        backgroundColor = curGoal.backgroundColor,
                                        isLiked = post.peopleLiked.contains(id)
                                    )
                                }
                            }
                            PostType.HABIT_ACHIEVEMENT -> {
                                val habit = getHabitById(post.achievementId.orEmpty())
                                habit?.let { curHabit ->
                                    PostResponse(
                                        dateOfCreation = post.dateOfCreation,
                                        id = post.id,
                                        likes = post.peopleLiked.size,
                                        comments = comments.size,
                                        description = curHabit.title,
                                        authorImageUrl = userInfo.profileImageUrl,
                                        authorName = "${userInfo.firstName} ${userInfo.lastName}",
                                        authorUsername = userInfo.username,
                                        ownerId = post.ownerId,
                                        imageUrl = post.imageUrl,
                                        type = post.type,
                                        achievementId = post.achievementId,
                                        iconName = curHabit.iconName,
                                        backgroundColor = curHabit.backgroundColor,
                                        isLiked = post.peopleLiked.contains(id)
                                    )
                                }
                            }
                            else -> {
                                PostResponse(
                                    dateOfCreation = post.dateOfCreation,
                                    id = post.id,
                                    likes = post.peopleLiked.size,
                                    comments = comments.size,
                                    description = post.description,
                                    authorImageUrl = userInfo.profileImageUrl,
                                    authorName = "${userInfo.firstName} ${userInfo.lastName}",
                                    authorUsername = userInfo.username,
                                    ownerId = post.ownerId,
                                    imageUrl = post.imageUrl,
                                    type = post.type,
                                    achievementId = post.achievementId,
                                    isLiked = post.peopleLiked.contains(id)
                                )
                            }
                        }
                    }
                }.mapNotNull {
                    it
                }
            }.flatten().sortedByDescending { post ->
                post.dateOfCreation
            }
            call.respond(HttpStatusCode.OK, postsOfUsers)
        }

    }
}
