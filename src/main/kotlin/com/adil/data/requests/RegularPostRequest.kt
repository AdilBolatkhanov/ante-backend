package com.adil.data.requests

data class RegularPostRequest(
    val description: String,
    val imageUrl: String? = null
)