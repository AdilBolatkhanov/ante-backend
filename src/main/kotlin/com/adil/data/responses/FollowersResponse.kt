package com.adil.data.responses

data class FollowersResponse(
    val userId: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String?,
    val isFollowed: Boolean
)
