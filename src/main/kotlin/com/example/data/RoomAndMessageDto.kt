package com.example.data

import com.example.model.Message

@kotlinx.serialization.Serializable
data class RoomAndMessageDto(
    val roomId: String,
    val name: String,
    val message: Message?
)