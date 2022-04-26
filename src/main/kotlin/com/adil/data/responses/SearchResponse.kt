package com.adil.data.responses

data class SearchResponse(
    val id: String,
    val username: String,
    val name: String,
    val profileImageUrl: String?,
    val bio: String?
)
