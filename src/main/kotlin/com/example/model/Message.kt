package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int,
    val senderUid: String,
    var text: String,
    var time: String
) : Comparable<Message> {
    override fun compareTo(other: Message): Int {
        return other.id - this.id
    }
}