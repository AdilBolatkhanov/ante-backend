package com.adil.routing

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import com.adil.API_VERSION
import com.adil.security.AwsConfig
import com.adil.utils.Constants.AWS_S3_IMAGE_BUCKET
import com.adil.utils.Constants.AWS_S3_URL
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val IMAGE = "$API_VERSION/image"

fun Application.registerImageHandlingRoutes() {
    val awsConfig = AwsConfig()
    routing {
        authenticate {
            post("$IMAGE/upload") {
                val id = call.principal<UserIdPrincipal>()!!.name
                val multipartData = call.receiveMultipart()

                multipartData.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val fileBytes = part.streamProvider().readBytes()
                        val keyOfFile = "$id-${part.originalFileName}"

                        val request = PutObjectRequest {
                            bucket = AWS_S3_IMAGE_BUCKET
                            key = keyOfFile
                            this.body = ByteStream.fromBytes(fileBytes)
                        }

                        S3Client(awsConfig).use { s3 ->
                            s3.putObject(request)
                        }
                        call.respond(HttpStatusCode.OK, AWS_S3_URL + keyOfFile)
                    }
                }
            }
        }
    }
}