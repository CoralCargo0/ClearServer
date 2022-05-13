package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatWithLastMessage(
    val chatId: String,
    var chatName: String,
    var message: Message? = null
)

@Serializable
data class ChatsWithLastMessages(
    val chats: List<ChatWithLastMessage?>? = null
)