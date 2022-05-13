package com.example

import com.example.data.RoomAndMessageDto
import java.util.UUID

object RoomsHolder {
    private val rooms: HashMap<String, ChatRoom> = hashMapOf()
    private val tmpRooms: HashMap<String, ChatRoom> = hashMapOf()

    fun createNewRoom(connectionCode: String, user: String, users: UsersDataSource): String {
        println("createNewRoom")
        var uid = UUID.randomUUID().toString()
        while (rooms[uid] != null) {
            println("while")
            uid = UUID.randomUUID().toString()
        }
        tmpRooms[connectionCode] = ChatRoom(uid, hashMapOf(Pair(user, 0)), users)
        rooms[uid] = tmpRooms[connectionCode]!!
        return uid
    }

    fun connectToRoom(connectionCode: String, user: String): String {
        println("connectToRoom")
        val uid = tmpRooms[connectionCode]?.chatId
        return if (tmpRooms[connectionCode] == null) {
            ""
        } else {
            rooms[uid!!] = tmpRooms[connectionCode]!!
            rooms[uid]?.users?.set(user, 0)
            tmpRooms.remove(connectionCode)
            uid
        }
    }

    fun getRooms(ids: List<String>): List<ChatRoom> {
        val newList = mutableListOf<ChatRoom>()
        ids.forEach {
            if (rooms[it] != null) newList.add(rooms[it]!!)
        }
        return newList
    }

    fun getRoomsIdsAndLastMessage(ids: List<String>): List<RoomAndMessageDto> {
        val newList = mutableListOf<RoomAndMessageDto>()
        var tmp: RoomAndMessageDto
        ids.forEach {
            if (rooms[it] != null) {
                tmp = RoomAndMessageDto(rooms[it]!!.chatId, "Chat", rooms[it]!!.getLastMessage())
                newList.add(tmp)
            }
        }
        return newList
    }

    fun getRoom(id: String): ChatRoom? {
        rooms.forEach {
            println(it.key)
        }
        return rooms[id]
    }
}