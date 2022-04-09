package com.adil.routing

import com.adil.API_VERSION
import com.adil.data.*
import com.adil.data.collections.Habit
import com.adil.data.requests.AddHabitRequest
import com.adil.data.requests.EditHabitRequest
import com.adil.data.requests.IdRequest
import com.adil.data.responses.HabitResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.Calendar

const val HABIT = "$API_VERSION/habit"

fun Application.registerHabitRoutes() {
    routing {
        authenticate {
            habitsRoutes()
        }
    }
}

fun Route.habitsRoutes(){
    route(HABIT){
        get {
            val habitId = call.request.queryParameters["id"] ?:  return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

            val habit = getHabitById(habitId) ?: return@get call.respond(HttpStatusCode.BadRequest, "Habit with this id doesn't exist")
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val lastMarkedDay = Calendar.getInstance().apply {
                timeInMillis = habit.lastTimeMarked
            }.get(Calendar.DAY_OF_YEAR)
            val canMarkToday = currentDay > lastMarkedDay
            val response = HabitResponse(
                title = habit.title,
                id = habit.id,
                backgroundColor = habit.backgroundColor,
                tag = habit.tag,
                iconName = habit.iconName,
                startDate = habit.startDate,
                targetNumOfDays = habit.targetNumOfDays,
                curNumOfDays = habit.curNumOfDays,
                ownerId = habit.ownerId,
                isPrivate = habit.isPrivate,
                canMarkToday = canMarkToday
            )
            call.respond(HttpStatusCode.OK, response)
        }

        post {
            val myId = call.principal<UserIdPrincipal>()!!.name
            val habit = try {
                call.receive<AddHabitRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "You didn't pass id with request")
                return@post
            }
            val newHabit = Habit(
                title = habit.title,
                backgroundColor = habit.backgroundColor,
                iconName = habit.iconName,
                tag = habit.tag,
                startDate = System.currentTimeMillis(),
                targetNumOfDays = habit.targetNumOfDays,
                curNumOfDays = 0,
                ownerId = myId,
                lastTimeMarked = 0,
                isPrivate = habit.isPrivate
            )
            if (addHabit(newHabit))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        post("/edit") {
            val request = try {
                call.receive<EditHabitRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Body broken or some fields missing")
                return@post
            }

            val curHabit = getHabitById(request.id) ?: return@post call.respond(HttpStatusCode.BadRequest, "Habit with this id doesn't exist")
            val updatedHabit = Habit(
                title = request.title,
                backgroundColor = request.backgroundColor,
                iconName = request.iconName,
                tag = request.tag,
                startDate = curHabit.startDate,
                targetNumOfDays = request.targetNumOfDays,
                curNumOfDays = curHabit.curNumOfDays,
                ownerId = curHabit.ownerId,
                lastTimeMarked = curHabit.lastTimeMarked,
                isPrivate = request.isPrivate,
                id = curHabit.id
            )
            if (updateHabit(updatedHabit))
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

            val habit = getHabitById(request.id) ?: return@delete call.respond(HttpStatusCode.BadRequest, "Habit with this id doesn't exist")

            if (deleteHabit(request.id))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }

        post("/mark") {
            val request = try {
                call.receive<IdRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "You didn't pass id with request")
                return@post
            }

            val habit = getHabitById(request.id) ?: return@post call.respond(HttpStatusCode.BadRequest, "Habit with this id doesn't exist")

            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val lastMarkedDay = Calendar.getInstance().apply {
                timeInMillis = habit.lastTimeMarked
            }.get(Calendar.DAY_OF_YEAR)
            val canMarkToday = currentDay > lastMarkedDay
            if (markAsDoneHabit(habit, canMarkToday))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.Conflict)
        }
    }
}