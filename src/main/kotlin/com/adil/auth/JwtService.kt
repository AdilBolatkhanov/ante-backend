package com.adil.auth

import com.adil.data.collections.User
import com.adil.utils.Constants.ANTE_BACKEND
import com.adil.utils.Constants.AUTHENTICATION
import com.adil.utils.Constants.AUTH_CLAIM
import com.adil.utils.Constants.JWT_SECRET
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

class JwtService {

    private val issuer = ANTE_BACKEND
    private val jwtSecret = System.getenv(JWT_SECRET)
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User): String = JWT.create()
        .withSubject(AUTHENTICATION)
        .withIssuer(issuer)
        .withClaim(AUTH_CLAIM, user.id)
        .sign(algorithm)
}