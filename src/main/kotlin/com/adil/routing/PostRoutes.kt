package com.adil.routing

import com.adil.API_VERSION
import com.adil.data.*
import com.adil.data.collections.PostType
import com.adil.data.requests.AchievementPostRequest
import com.adil.data.requests.AchievementPostTypeRequest
import com.adil.data.requests.RegularPostRequest
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
        }
    }
}

fun Route.postsRoutes(){
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
            val res = when (request.type){
                AchievementPostTypeRequest.GOAL -> addAchievementPost(PostType.GOAL_ACHIEVEMENT, request.achievementId, id)
                else -> addAchievementPost(PostType.HABIT_ACHIEVEMENT, request.achievementId, id)
            }
            if (res)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        get {
            val id = call.principal<UserIdPrincipal>()!!.name

            val postsOfUser = getPostForUser(id).map { post ->
                val comments = getCommentsForPost(post.id)
                val userInfo = findUser(post.ownerId)
                userInfo?.let {
                    when (post.type){
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
                                    ownerId = id,
                                    imageUrl = post.imageUrl,
                                    type = post.type,
                                    achievementId = post.achievementId,
                                    iconName = curGoal.iconName,
                                    backgroundColor = curGoal.backgroundColor
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
                                    ownerId = id,
                                    imageUrl = post.imageUrl,
                                    type = post.type,
                                    achievementId = post.achievementId,
                                    iconName = curHabit.iconName,
                                    backgroundColor = curHabit.backgroundColor
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
                                ownerId = id,
                                imageUrl = post.imageUrl,
                                type = post.type,
                                achievementId = post.achievementId)
                        }
                    }
                }
            }.mapNotNull {
                it
            }.sortedByDescending { post ->
                post.dateOfCreation
            }

            call.respond(HttpStatusCode.OK, postsOfUser)
        }
    }
}
