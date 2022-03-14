package com.adil.routing

import com.adil.API_VERSION
import com.adil.data.*
import com.adil.data.requests.AccountLoginRequest
import com.adil.data.requests.EditProfileRequest
import com.adil.data.responses.GoalInProfile
import com.adil.data.responses.PostInProfile
import com.adil.data.responses.UserProfile
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
            val id = call.principal<UserIdPrincipal>()!!.name
            val userInfo = findUser(id)
            val habitsOfUser = getHabitsForUser(id)
            val goalsOfUser = getGoalsForUser(id).map { goal ->
                val subgoals = getSubGoalForGoal(goal.id).map { it.isCompleted }
                GoalInProfile(
                    subGoals = subgoals,
                    title = goal.title,
                    id = goal.id,
                    ownerId = goal.ownerId,
                    iconUrl = goal.iconUrl,
                    backgroundColor = goal.backgroundColor,
                    tag = goal.tag,
                    dueDate = goal.dueDate
                )
            }
            val postsOfUser = getPostForUser(id).map { post ->
                val comments = getCommentsForPost(post.id)
                PostInProfile(
                    dateOfCreation = post.dateOfCreation,
                    id = post.id,
                    likes = post.peopleLiked.size,
                    comments = comments.size,
                    description = post.description,
                    authorImageUrl = userInfo?.profileImageUrl.orEmpty(),
                    authorName = "${userInfo?.firstName.orEmpty()} ${userInfo?.lastName.orEmpty()}",
                    authorUsername = userInfo?.username.orEmpty(),
                    ownerId = id,
                    imageUrl = post.imageUrl
                )
            }
            val userProfile = UserProfile(
                userId = id,
                username = userInfo?.username.orEmpty(),
                firstName = userInfo?.firstName.orEmpty(),
                lastName = userInfo?.lastName.orEmpty(),
                dateOfBirth = userInfo?.dateOfBirth,
                backgroundUrl = userInfo?.backgroundUrl,
                profileImageUrl = userInfo?.profileImageUrl,
                bio = userInfo?.bio,
                followers = userInfo?.followers ?: emptyList(),
                following = userInfo?.following ?: emptyList(),
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
            request.profileImageUrl?.let { image -> updateUserBackground(id, image) }
            call.respond(HttpStatusCode.OK)
        }
    }
}