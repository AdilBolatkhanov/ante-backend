package com.adil

import aws.sdk.kotlin.runtime.auth.credentials.CredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import com.adil.auth.JwtService
import com.adil.data.addImage
import com.adil.data.collections.Image
import com.adil.data.findUser
import com.adil.data.getImage
import com.adil.routing.registerProfileRoutes
import com.adil.routing.registerUserRoutes
import com.adil.utils.Constants
import com.adil.utils.Constants.ANTE_BACKEND
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
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
                    UserIdPrincipal(user.id)
                else
                    null
            }
        }
    }
    val awsConfig = AwsConfig()
    registerUserRoutes(jwtService)
    registerProfileRoutes()
    routing {
        authenticate {
            get("/") {
                val email = call.principal<UserIdPrincipal>()!!.name
                call.respondText("HELLO WORLD! $email")
            }
        }
        post("/upload"){
            val multipartData = call.receiveMultipart()

            multipartData.forEachPart { part ->
                if (part is PartData.FileItem) {
                        val fileBytes = part.streamProvider().readBytes()
                        val image = Image(fileBytes)
                        //addImage(image)
                        val request = PutObjectRequest {
                            bucket = "profile-ante"
                            key = part.originalFileName
                            this.body = ByteStream.fromBytes(fileBytes)
                        }

                        S3Client { region = "eu-west-2"
                        credentialsProvider = awsConfig.credentialsProvider}.use { s3 ->
                            val response = s3.putObject(request)
                            println("Tag information is ${response.eTag}")
                        }
                    }
            }
        }
        get("/image/{id}") {
            val id = call.parameters["id"]  ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Missing Fields"
            )
            val image = getImage(id)
            if (image != null){
                call.respond(image.data)
            }else{
                call.respond(HttpStatusCode.BadRequest, "Problems creating user")
            }
        }
    }
}

const val API_VERSION = "/v1"
