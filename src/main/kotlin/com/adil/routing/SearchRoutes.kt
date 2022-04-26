package com.adil.routing

import com.adil.API_VERSION
import com.adil.data.*
import com.adil.data.collections.Goal
import com.adil.data.collections.SubGoal
import com.adil.data.requests.AddGoalRequest
import com.adil.data.requests.AddHabitRequest
import com.adil.data.requests.EditGoalRequest
import com.adil.data.requests.IdRequest
import com.adil.data.responses.GoalDetailResponse
import com.adil.data.responses.SearchResponse
import com.adil.data.responses.SubGoalDetailResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val SEARCH = "$API_VERSION/search"

fun Application.registerSearchRoutes() {
    routing {
        authenticate {
            searchRoutes()
        }
    }
}

fun Route.searchRoutes(){
    route(SEARCH){
        get {
            val myId = call.principal<UserIdPrincipal>()!!.name
            val myProfile = findUser(myId) ?: return@get call.respond(HttpStatusCode.BadRequest, "No user with such id exists")
            val query = call.request.queryParameters["query"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing query")

            val searchResult = searchUser(query).map { user ->
                SearchResponse(
                    id = user.id,
                    name = "${user.firstName} ${user.lastName}",
                    username = user.username,
                    profileImageUrl = user.profileImageUrl,
                    bio = user.bio,
                    isFollowed = myProfile.following.contains(user.id)
                )
            }
            call.respond(HttpStatusCode.OK, searchResult)
        }
    }
}
