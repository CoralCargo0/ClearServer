package com.example

import com.example.model.Message
import com.example.model.ChatWithLastMessage
import java.util.Calendar

class ChatRoom(
    val chatId: String,
    val users: HashMap<String, Int>,
    private val usersDataSource: UsersDataSource
) {
    private var messages: MutableList<Message> = mutableListOf()

    var listener: OnNewMessageAppearsListener? = null

    private fun getLastMessageId(): Int {
        return if (messages.isEmpty()) {
            0
        } else
            messages.last().id
    }

    fun checkAmount(uid: String): Pair<Int, Int> {
        val pair = Pair(getLastMessageId(), users[uid] ?: 0)
        users[uid] = getLastMessageId()
        return pair
    }

    fun getUserWithMessage(uid: String): ChatWithLastMessage? {
        val message = getLastMessage()
        val user: String = getCompanion(uid)
        return if (message == null && user.isEmpty()) null
        else ChatWithLastMessage(chatId, user.let { usersDataSource[it]?.nickname ?: chatId.take(8) }, message)
    }

    private fun getCompanion(uid: String): String {
        var companion = ""
        users.forEach { (key, _) ->
            if (key != uid) {
                companion = key
                return@forEach
            }
        }
        return companion
    }

    fun getCompanionSocket(uid: String): UserSocket {
        var companion = ""
        users.forEach { (key, _) ->
            if (key != uid) {
                companion = key
                return@forEach
            }
        }
        return usersDataSource[companion]!!
    }

    fun getLastMessage(): Message? {
        return if (messages.isEmpty()) {
            null
        } else
            messages.last()
    }

    fun getLastMessages(amount: Int): List<Message> {
        return messages.takeLast(amount)
    }

    fun addMessage(
        _senderUid: String,
        _text: String
    ) {
        messages.add(
            Message(
                getLastMessageId() + 1,
                _senderUid,
                _text,
                Calendar.getInstance().timeInMillis.toString()
            )
        )
        if (listener != null) {
            users.forEach { (key, _) ->
                println("New message appears uid - $key --- ")
                listener!!.onNewMessageAppears(
                    chatId, users.map { key }
                )
            }
        }
    }

    fun getMessages(): List<Message> {
        var retList = messages.toList()
        retList = retList.subList(retList.lastIndex - 5, retList.lastIndex)
        return retList
    }

    fun setMessageListener(_listener: OnNewMessageAppearsListener) {
        listener = _listener
    }

    interface OnNewMessageAppearsListener {
        fun onNewMessageAppears(chatId: String, users: List<String>)
    }
}