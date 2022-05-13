package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userUid: String,
    var email: String,
    var password: String,
    var nickname: String = ""
)