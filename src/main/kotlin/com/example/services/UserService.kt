package com.example.services

interface UserService {
    fun login(userId: Int): String?
    fun auth(sessionKey: String?): Int?
}