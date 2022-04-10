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
import com.adil.data.responses.SubGoalDetailResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val GOAL = "$API_VERSION/goal"

fun Application.registerGoalRoutes() {
    routing {
        authenticate {
            goalRoutes()
        }
    }
}

fun Route.goalRoutes(){
    route(GOAL){
        get{
            val goalId = call.request.queryParameters["id"] ?:  return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

            val goal = getGoalById(goalId) ?: return@get call.respond(HttpStatusCode.BadRequest, "Goal with this id doesn't exist")
            val subGoals = getSubGoalForGoal(goalId).sortedBy { it.dueDate }

            val response = GoalDetailResponse(
                title = goal.title,
                id = goal.id,
                dueDate = goal.dueDate,
                tag = goal.tag,
                backgroundColor = goal.backgroundColor,
                iconName = goal.iconName,
                ownerId = goal.ownerId,
                isPrivate = goal.isPrivate,
                subGoals = subGoals.map { subGoal ->
                    SubGoalDetailResponse(
                        title = subGoal.title,
                        goalId = subGoal.goalId,
                        dueDate = subGoal.dueDate,
                        isCompleted = subGoal.isCompleted,
                        id = subGoal.id
                    )
                }
            )
            call.respond(HttpStatusCode.OK, response)
        }

        post {
            val myId = call.principal<UserIdPrincipal>()!!.name
            val addGoalRequest = try {
                call.receive<AddGoalRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "You didn't pass id with request")
                return@post
            }

            val goal = Goal(
                title = addGoalRequest.title,
                dueDate = addGoalRequest.dueDate,
                tag = addGoalRequest.tag,
                backgroundColor = addGoalRequest.backgroundColor,
                iconName = addGoalRequest.iconName,
                ownerId = myId,
                isPrivate = addGoalRequest.isPrivate,
            )
            val goalAdded = addGoal(goal)
            val subGoals = addGoalRequest.subGoal.map { request ->
                SubGoal(
                    title = request.title,
                    dueDate = request.dueDate,
                    isCompleted = request.isCompleted,
                    goalId = goal.id
                )
            }
            val subGoalsAdded = addSubGoals(subGoals)
            if (goalAdded && subGoalsAdded)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        post("/edit") {
            val myId = call.principal<UserIdPrincipal>()!!.name
            val editGoalRequest = try {
                call.receive<EditGoalRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "You didn't pass id with request")
                return@post
            }
            val goal = getGoalById(editGoalRequest.id) ?: return@post call.respond(HttpStatusCode.BadRequest, "Goal with this id doesn't exist")
            val deletedSubgoals = deleteSubGoalsForGoal(editGoalRequest.id)

            val updatedGoal = Goal(
                id = editGoalRequest.id,
                title = editGoalRequest.title,
                dueDate = editGoalRequest.dueDate,
                tag = editGoalRequest.tag,
                backgroundColor = editGoalRequest.backgroundColor,
                iconName = editGoalRequest.iconName,
                ownerId = myId,
                isPrivate = editGoalRequest.isPrivate,
            )
            val updateGoal = updateGoal(updatedGoal)
            val subGoals = editGoalRequest.subGoal.map { request ->
                SubGoal(
                    title = request.title,
                    dueDate = request.dueDate,
                    isCompleted = request.isCompleted,
                    goalId = goal.id
                )
            }
            val subGoalsAdded = addSubGoals(subGoals)
            if (deletedSubgoals && updateGoal && subGoalsAdded)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        delete {
            val request = try {
                call.receive<IdRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "You didn't pass id with request")
                return@delete
            }

            val goal = getGoalById(request.id) ?: return@delete call.respond(HttpStatusCode.BadRequest, "Goal with this id doesn't exist")
            if (deleteGoal(request.id) && deleteSubGoalsForGoal(request.id))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        post("/complete") {
            val request = try {
                call.receive<IdRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "You didn't pass id with request")
                return@post
            }
            if (completeSubGoal(request.id))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }
    }
}
