package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val chatId: String,
    val id: Int,
    val senderUid: String,
    var text: String,
    var time: String
)