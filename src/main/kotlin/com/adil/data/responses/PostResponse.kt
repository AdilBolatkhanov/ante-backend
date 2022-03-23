package com.adil.data.responses

import com.adil.data.collections.PostType

data class PostResponse(
    val dateOfCreation: Long,
    val authorName: String,
    val authorUsername: String,
    val authorImageUrl: String?,
    val likes: Int,
    val comments: Int,
    val description: String,
    val ownerId: String,
    var imageUrl: String? = null,
    val type: PostType,
    val achievementId: String? = null,
    val iconName: String? = null,
    val backgroundColor: String? = null,
    val id: String
)

