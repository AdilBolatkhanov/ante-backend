package com.adil

import com.adil.auth.JwtService
import com.adil.data.findUser
import com.adil.routing.registerUserRoute
import com.adil.utils.Constants
import com.adil.utils.Constants.ANTE_BACKEND
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.slf4j.event.Level

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(CallLogging)
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson { setPrettyPrinting() }
    }

    val jwtService = JwtService()
    install(Authentication){
        jwt {
            verifier(jwtService.verifier)
            realm = ANTE_BACKEND
            validate {
                val payload = it.payload
                val claim = payload.getClaim(Constants.AUTH_CLAIM)
                val user = findUser(claim.asString())
                if (user != null)
                    UserIdPrincipal(user.email)
                else
                    null
            }
        }
    }

    registerUserRoute(jwtService)
    routing {
        authenticate {
            get("/") {
                val email = call.principal<UserIdPrincipal>()!!.name
                call.respondText("HELLO WORLD! $email")
            }
        }
    }
}

const val API_VERSION = "/v1"
