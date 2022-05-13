package com.example.model

import kotlinx.serialization.Serializable

@Serializable
class MessagesDto(
    val messages: List<MessageDto>
)