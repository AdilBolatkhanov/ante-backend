package com.adil.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.Binary
import org.bson.types.ObjectId

data class Image(
    val data: ByteArray,
    @BsonId
    val id: String = ObjectId().toString()
)
