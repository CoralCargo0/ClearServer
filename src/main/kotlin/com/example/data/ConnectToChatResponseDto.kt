package com.example.data

@kotlinx.serialization.Serializable
data class ConnectToChatResponseDto(
    val chatId: String,
    val userName: String
)