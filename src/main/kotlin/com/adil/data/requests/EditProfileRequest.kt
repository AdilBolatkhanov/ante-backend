package com.adil.data.requests

data class EditProfileRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Long?,
    val backgroundUrl: String?,
    val profileImageUrl: String?,
    val bio: String?,
)