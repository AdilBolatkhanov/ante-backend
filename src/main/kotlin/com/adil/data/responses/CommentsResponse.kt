package com.adil.data.responses

data class CommentsResponse(
    val dateOfCreation: Long,
    val authorName: String,
    val authorUsername: String,
    val authorImageUrl: String?,
    val likes: Int,
    val text: String,
    val isLiked: Boolean,
    val isHelpful: Boolean,
    val id: String
)
