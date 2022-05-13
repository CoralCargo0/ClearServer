package com.example

import com.example.model.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileReader
import java.io.FileWriter

class UsersDataSource {
    private val users = hashMapOf<String, UserSocket>()
    private var fileWriter: FileWriter

    operator fun get(uid: String): UserSocket? = users[uid]

    fun addUser(
        userUid: String,
        email: String,
        password: String,
        nickname: String
    ): Boolean {
        return if (users[userUid] != null) {
            false
        } else {
            users[userUid] = UserSocket(userUid, email, password, nickname)
            fileWriter.write(Json.encodeToString(User(userUid, email, password, nickname)))
            fileWriter.write(
                "\n"
            )
            fileWriter.flush()
            true
        }
    }

    fun login(
        email: String,
        password: String
    ): String? {
        users.forEach {
            if (it.value.checkPassword(email, password)) {
                return it.key
            }
        }
        return null
    }

    init {
        var userTMP: User
        val saveFile = FileReader("userdb.txt").readLines()
        fileWriter = FileWriter("userdb.txt")
        saveFile.forEach { _user ->
            userTMP = Json.decodeFromString(User.serializer(), _user)
            userTMP.apply {
                users[userUid] = UserSocket(userUid, email, password, nickname)
            }
            fileWriter.write(_user)
            fileWriter.write("\n")
            fileWriter.flush()
        }
    }
}