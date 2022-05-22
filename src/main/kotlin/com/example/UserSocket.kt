package com.example

import com.example.common.Constants.SOCKET_COMMAND_RECEIVE_CHATS
import com.example.model.ChatsWithLastMessages
import com.example.model.MessageDto
import com.example.model.MessagesDto
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class UserSocket(
    private val userUid: String,
    private var email: String,
    private var password: String,
    var nickname: String = ""
) {
    private var socketListener: ServerSocket? = null
    private var socket: Socket? = null
    private var port: Int = 8083
    private var isFree: Boolean = true
    private val chatRooms: HashMap<String, ChatRoom> = hashMapOf()
    private var ois: BufferedReader? = null
    private var ous: BufferedWriter? = null
    var initSock = false
    fun connectToSocket(): Int {
        if (isFree) {
//            socket?.close()
//            socketListener?.close()
//            socketListener?.channel?.close()
//            socketListener = null
//            socket = null
            try {
                println("trying opem socket with port  --- $port")
                if (initSock) {
                    socketListener = ServerSocket(port)
                    initSock = false
                }

                CoroutineScope(Dispatchers.IO).launch {
                    holdSocket()
                }
                isFree = false
            } catch (e: IOException) {
                println(e)
                return connectToSocket()
            }
            return port
        } else {
            socket?.close()
            socketListener?.close()
            isFree = true
            return connectToSocket()
        }
    }

    private suspend fun holdSocket() {
        try {
            socket =
                socketListener?.accept()

            // socketListener = null

            println("Accept a client!")
            ois = BufferedReader(InputStreamReader(socket?.getInputStream()))
            ous = BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))

//        chatRooms.forEach { (k, v) ->
//            v.getCompanionSocket(userUid).sendString(SOCKET_COMMAND_USER_ONLINE + k)
//        }

            ous?.write(
                SOCKET_COMMAND_RECEIVE_CHATS
            )
            sendUserWithMessages(getUsersWithMessages())

        } catch (e: Throwable) {
            println("Error in holdsocket - ${e.message}")
            initSock = true
            return
        }

        var line: String?

        println("isSocketOpen after try-catch  ------ ${isSocketOpened()}")
        var messageDto: MessageDto?
        while (isSocketOpened()) {
            line = try {
                ois?.readLine()
            } catch (e: SocketException) {
                null
            }
            if (line != null) {
                if (line == "QUIT") {
                    println(line)
                    withContext(Dispatchers.IO) {
                        socket!!.close()
                    }
                    isFree = true
                    break
                }
                try {
                    println(line)
                    messageDto = Json.decodeFromString(MessageDto.serializer(), line)

                    chatRooms[messageDto.chatId]?.addMessage(messageDto.senderUid, messageDto.text)

                    chatRooms.forEach {
                        println(it.key)
                    }
                } catch (e: Throwable) {
                    println(e.message)
                }

            }
            delay(100)
        }
        socket!!.close()
        println(socket!!.isClosed)
        isFree = true
        socket = null
        socketListener = null
//        chatRooms.forEach { (k, v) ->
//            v.getCompanionSocket(userUid).sendString(SOCKET_COMMAND_USER_OFFLINE + k)
//        }
        return
    }

    private fun isSocketOpened(): Boolean {
        return !socket!!.isClosed &&
                socket!!.isConnected
    }

    fun checkNewMessages(chatId: String) {
        var tmpPair: Pair<Int, Int>
        chatRooms[chatId]?.apply {
            tmpPair = this.checkAmount(userUid)
            if (tmpPair.first > tmpPair.second) {
                sendList(MessagesDto(this.getLastMessages(tmpPair.first - tmpPair.second).map {
                    MessageDto(chatId, it.id, it.senderUid, it.text, it.time)
                }))

            }
        }
    }

    private fun sendList(mess: MessagesDto) {
        ous?.write(Json.encodeToString(mess))
        ous?.newLine()
        ous?.flush()
    }

    fun sendString(mess: String) {
        if (isSocketOpened()) {
            ous?.write(mess)
            ous?.newLine()
            ous?.flush()
        }
    }

    private fun sendUserWithMessages(usersWithMessages: ChatsWithLastMessages) {
        if (isSocketOpened()) {
            ous?.write(Json.encodeToString(usersWithMessages))
            ous?.newLine()
            ous?.flush()
        }
    }


    fun addRoom(room: ChatRoom?) {
        if (room != null) {
            println("---Add room --- ${room.chatId}")
            chatRooms[room.chatId] = room
        }
    }

    init {
        port = lastUsedPort++
        socketListener = ServerSocket(port)
    }

    fun checkPassword(_email: String, _password: String): Boolean = email == _email && password == _password

    fun getUsersWithMessages() = ChatsWithLastMessages(chatRooms.map { it.value.getUserWithMessage(userUid) })

    companion object {
        private var lastUsedPort: Int = 8083
    }
}