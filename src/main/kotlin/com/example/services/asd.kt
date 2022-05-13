package com.example.services

import com.example.RoomsHolder
import com.example.UsersDataSource
import com.example.ChatRoom
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.example.common.Constants
import com.example.data.ConnectToChatResponseDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.model.ChatsWithLastMessages
import java.io.IOException
import java.util.*


class MainResource(
    private val users: UsersDataSource
) : HttpHandler {

    private val holder = RoomsHolder

    //val userForTest = com.example.UserSocket("12363", "trokay@tut.by", "12345678", "30")
    //val userForTest2 = com.example.UserSocket("412424", "geloyakort@gmail.com", "qwertyuiop", "31")

    private val newMessageListener = object : ChatRoom.OnNewMessageAppearsListener {
        override fun onNewMessageAppears(chatId: String, users: List<String>) {
            users.forEach { userId ->
                this@MainResource.users[userId]?.checkNewMessages(chatId)
            }
        }

    }
    val PARAMETERS = "params"
    val LEVEL_ID = "levelId"
    val REQUEST_TYPE = "request_type"

    @Throws(IOException::class)
    override fun handle(httpExchange: HttpExchange) {
        var response: String? = ""
        var httpCode = 200
        httpExchange.responseHeaders.add("Content-Type", "text/plain")
        val params = httpExchange.getAttribute(PARAMETERS) as Map<String, String>
        params.forEach {
            println("${it.key} - ${it.value}")
        }
        try {
            if (params.size >= 2) {
                when (params[LEVEL_ID]) {
                    "user" -> {
                        if (params[REQUEST_TYPE] == "register") {
                            val email = params["email"]
                            val password = params["password"]
                            val nickname = params["nickname"]
                            response =
                                if (email.isNullOrEmpty() || password.isNullOrEmpty() || nickname.isNullOrEmpty()) {
                                    Constants.NOT_FOUND_ERROR_MESSAGE_RESPONSE
                                } else {
                                    var uid = UUID.randomUUID().toString().take(8)
                                    while (users[uid] != null) {
                                        uid = UUID.randomUUID().toString()
                                    }
                                    users.addUser(uid, email, password, nickname)
                                    uid
                                }
                            println(response)


                        } else if (params[REQUEST_TYPE] == "login") {
                            val email = params["email"]
                            val password = params["password"]
                            println("$email + $password")
                            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                                response = ""
                            } else {
                                response =
                                    users.login(email, password) ?: Constants.NOT_FOUND_ERROR_MESSAGE_RESPONSE

                            }
                            println(response)

                        }
                    }
                    "room"
                    -> {
                        if (params[REQUEST_TYPE] == "create") {

                            val uid = params["uid"]
                            val connectionCode = params["code"]
                            //println("Create $uid + $connectionCode --- ${users[uid]?.nickname}")
                            response =
                                if (!connectionCode.isNullOrEmpty() && !uid.isNullOrEmpty()) {
                                    val roomUid = RoomsHolder.createNewRoom(connectionCode, uid, users)
                                    users[uid]?.addRoom(RoomsHolder.getRoom(roomUid))
                                    RoomsHolder.getRoom(roomUid)?.setMessageListener(newMessageListener)
                                    roomUid
                                } else {
                                    Constants.NOT_FOUND_ERROR_MESSAGE_RESPONSE
                                }

                            println(response)
                        } else if (params[REQUEST_TYPE] == "connect") {

                            val uid = params["uid"]
                            val connectionCode = params["code"]
                            println("Connect $uid + $connectionCode")
                            response =
                                Json.encodeToString(
                                    if (!connectionCode.isNullOrEmpty() && !uid.isNullOrEmpty()) {
                                        val roomUid = RoomsHolder.connectToRoom(connectionCode, uid)
                                        RoomsHolder.getRoom(roomUid)?.users?.forEach { (k, v) ->
                                            if (k != uid) {
                                                users[k]?.sendString("${Constants.SOCKET_COMMAND_USER_CONNECTED}/$roomUid/${users[uid]?.nickname}")
                                            }
                                        }
                                        users[uid]?.addRoom(RoomsHolder.getRoom(roomUid))
                                        println("================================" + roomUid)
                                        ConnectToChatResponseDto(
                                            roomUid,
                                            RoomsHolder.getRoom(roomUid)?.getCompanionSocket(uid)?.nickname ?: ""
                                        )
                                    } else {
                                        println("================================" + Constants.NOT_FOUND_ERROR_MESSAGE_RESPONSE)
                                        ConnectToChatResponseDto(
                                            Constants.NOT_FOUND_ERROR_MESSAGE_RESPONSE,
                                            Constants.NOT_FOUND_ERROR_MESSAGE_RESPONSE
                                        )
                                    }
                                )
                            println(response)
                        }
                    }
                    "rooms"
                    -> {
                        if (params[REQUEST_TYPE] == "getUsersWithMessage") {

                            val uid = params["uid"]
                            //println("rooms - " + uid)
                            response =
                                if (uid != null) {
                                    val dd: ChatsWithLastMessages =
                                        users[uid]?.getUsersWithMessages() ?: ChatsWithLastMessages()
                                    println("rooms -${Json.encodeToString(dd)}")
                                    Json.encodeToString(dd)
                                } else Constants.NOT_FOUND_ERROR_MESSAGE_RESPONSE
                            println(response)
                        } else if (params[REQUEST_TYPE] == "login") {
                            println(response)
                        }
                    }
                    "port"
                    -> {
                        if (params[REQUEST_TYPE] == "get") {
                            val uid = params["uid"]
                            println(uid + "=======HTTPServer")
                            response =
                                if (uid != null) {
                                    users[uid]?.connectToSocket().toString()
                                } else Constants.NOT_FOUND_ERROR_MESSAGE_RESPONSE
                            println(response)
                        }
                    }

                }
            }
        } catch (iaex: IllegalArgumentException) {
            response = "Precondition failed"
            httpCode = 412
        } catch (ex: Exception) {
            response = "Something wrong happened"
            httpCode = 500
        }
        httpExchange.sendResponseHeaders(httpCode, response!!.length.toLong())
        val os = httpExchange.responseBody
        os.write(response.toByteArray())
        os.close()
    }
}