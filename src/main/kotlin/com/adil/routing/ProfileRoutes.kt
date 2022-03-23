package com.adil.routing

import com.adil.API_VERSION
import com.adil.data.*
import com.adil.data.requests.EditProfileRequest
import com.adil.data.requests.UserIdRequest
import com.adil.data.responses.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PROFILE = "$API_VERSION/profile"

fun Application.registerProfileRoutes() {
    routing {
        authenticate {
            getProfileInfo()
        }
    }
}

fun Route.getProfileInfo() {
    route(PROFILE) {
        get {
            val id = call.request.queryParameters["id"] ?:  return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
            val userInfo =
                findUser(id) ?: return@get call.respond(HttpStatusCode.BadRequest, "No user with such id exists")
            val habitsOfUser = getHabitsForUser(id)
            val goalsOfUser = getGoalsForUser(id).map { goal ->
                val subgoals = getSubGoalForGoal(goal.id).map { it.isCompleted }
                GoalInProfile(
                    subGoals = subgoals,
                    title = goal.title,
                    id = goal.id,
                    ownerId = goal.ownerId,
                    iconName = goal.iconName,
                    backgroundColor = goal.backgroundColor,
                    tag = goal.tag,
                    dueDate = goal.dueDate,
                    isPrivate = goal.isPrivate
                )
            }.sortedBy { goal ->
                goal.dueDate
            }
            val postsOfUser = getPostForUser(id).map { post ->
                val comments = getCommentsForPost(post.id)
                PostInProfile(
                    dateOfCreation = post.dateOfCreation,
                    id = post.id,
                    likes = post.peopleLiked.size,
                    comments = comments.size,
                    description = post.description,
                    authorImageUrl = userInfo.profileImageUrl,
                    authorName = "${userInfo.firstName} ${userInfo.lastName}",
                    authorUsername = userInfo.username,
                    ownerId = id,
                    imageUrl = post.imageUrl
                )
            }.sortedByDescending { post ->
                post.dateOfCreation
            }
            val userProfile = UserProfile(
                userId = id,
                username = userInfo.username,
                firstName = userInfo.firstName,
                lastName = userInfo.lastName,
                dateOfBirth = userInfo.dateOfBirth,
                backgroundUrl = userInfo.backgroundUrl,
                profileImageUrl = userInfo.profileImageUrl,
                bio = userInfo.bio,
                followers = userInfo.followers,
                following = userInfo.following,
                habits = habitsOfUser,
                posts = postsOfUser,
                goals = goalsOfUser
            )
            call.respond(HttpStatusCode.OK, userProfile)
        }

        post("/edit") {
            val id = call.principal<UserIdPrincipal>()!!.name
            val request = try {
                call.receive<EditProfileRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            updateUserUsername(id, request.username)
            request.bio?.let { bio -> updateUserBio(id, bio) }
            updateUserFirstName(id, request.firstName)
            updateUserLastName(id, request.lastName)
            request.dateOfBirth?.let { date -> updateUserDateOfBirth(id, date) }
            request.backgroundUrl?.let { background -> updateUserBackground(id, background) }
            request.profileImageUrl?.let { image -> updateUserProfileImage(id, image) }
            call.respond(HttpStatusCode.OK)
        }

        get("/followers") {
            val userId = call.request.queryParameters["id"] ?:  return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
            val myId = call.principal<UserIdPrincipal>()!!.name
            val user = findUser(userId) ?: return@get call.respond(
                HttpStatusCode.BadRequest,
               "No user with such id exists"
            )
            val me = findUser(myId) ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "No user with such id exists"
            )

            val followers = user.followers.mapNotNull { followerId ->
                findUser(followerId)
            }.map { followerInfo ->
                FollowersResponse(
                    username = followerInfo.username,
                    userId = followerInfo.id,
                    firstName = followerInfo.firstName,
                    lastName = followerInfo.lastName,
                    profileImageUrl = followerInfo.profileImageUrl,
                    isFollowed = me.following.contains(followerInfo.id)
                )
            }
            call.respond(HttpStatusCode.OK, followers)
        }

        get("/following") {
            val userId = call.request.queryParameters["id"] ?:  return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

            val myId = call.principal<UserIdPrincipal>()!!.name
            val user = findUser(userId) ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "No user with such id exists"
            )
            val me = findUser(myId) ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "No user with such id exists"
            )

            val following = user.following.mapNotNull { followingId ->
               findUser(followingId)
            }.map { followingUser ->
                FollowersResponse(
                    username = followingUser.username,
                    userId = followingUser.id,
                    firstName = followingUser.firstName,
                    lastName = followingUser.lastName,
                    profileImageUrl = followingUser.profileImageUrl,
                    isFollowed = me.following.contains(followingUser.id)
                )
            }
            call.respond(HttpStatusCode.OK, following)
        }

        post("/follow") {
            val request = try {
                call.receive<UserIdRequest>()
            } catch (e: ContentTransformationException) {
                return@post call.respond(HttpStatusCode.BadRequest, "Missing id")
            }
            val userId = request.userId
            val myId = call.principal<UserIdPrincipal>()!!.name

            val userExist = findUser(userId) != null
            if (!userExist){
                call.respond(
                    HttpStatusCode.BadRequest,
                    "No user with this id exists"
                )
                return@post
            }
            if (checkIfAlreadyFollowed(myId, userId)){
                call.respond(
                    HttpStatusCode.Conflict, "You have already followed"
                )
                return@post
            }
            if (followUser(myId, userId)){
                call.respond(
                    HttpStatusCode.OK,
                   "Successfully followed"
                )
            }else{
                call.respond(
                    HttpStatusCode.Conflict,
                    "Something went wrong during following"
                )
            }
        }

        post("/unfollow") {
            val request = try {
                call.receive<UserIdRequest>()
            } catch (e: ContentTransformationException) {
                return@post call.respond(HttpStatusCode.BadRequest, "Missing id")
            }
            val userId = request.userId
            val myId = call.principal<UserIdPrincipal>()!!.name

            val userExist = findUser(userId) != null
            if (!userExist){
                call.respond(
                    HttpStatusCode.BadRequest,
                   "No user with this id exists"
                )
                return@post
            }

            if (unfollowUser(myId, userId)){
                call.respond(
                    HttpStatusCode.OK,
                    "Successfully unfollowed"
                )
            }else{
                call.respond(
                    HttpStatusCode.Conflict,
                    "Something went wrong during unfollowing"
                )
            }
        }
    }
}