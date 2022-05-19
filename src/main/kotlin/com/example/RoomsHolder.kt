package com.example

import java.util.UUID

object RoomsHolder {
    private val rooms: HashMap<String, ChatRoom> = hashMapOf()
    private val waitingRooms: HashMap<String, ChatRoom> = hashMapOf()

    fun createNewRoom(connectionCode: String, user: String, users: UsersDataSource): String {
        println("createNewRoom")
        var uid = UUID.randomUUID().toString()
        while (rooms[uid] != null) {
            println("while")
            uid = UUID.randomUUID().toString()
        }
        waitingRooms[connectionCode] = ChatRoom(uid, hashMapOf(Pair(user, 0)), users)
        rooms[uid] = waitingRooms[connectionCode]!!
        return uid
    }

    fun connectToRoom(connectionCode: String, user: String): String {
        println("connectToRoom")
        val uid = waitingRooms[connectionCode]?.chatId
        return if (waitingRooms[connectionCode] == null) {
            ""
        } else {
            rooms[uid!!] = waitingRooms[connectionCode]!!
            rooms[uid]?.users?.set(user, 0)
            waitingRooms.remove(connectionCode)
            uid
        }
    }

//    fun getRooms(ids: List<String>): List<ChatRoom> {
//        val newList = mutableListOf<ChatRoom>()
//        ids.forEach {
//            if (rooms[it] != null) newList.add(rooms[it]!!)
//        }
//        return newList
//    }
//
//    fun getRoomsIdsAndLastMessage(ids: List<String>): List<RoomAndMessageDto> {
//        val newList = mutableListOf<RoomAndMessageDto>()
//        var tmp: RoomAndMessageDto
//        ids.forEach {
//            if (rooms[it] != null) {
//                tmp = RoomAndMessageDto(rooms[it]!!.chatId, "Chat", rooms[it]!!.getLastMessage())
//                newList.add(tmp)
//            }
//        }
//        return newList
//    }

    fun getRoom(id: String): ChatRoom? {
        rooms.forEach {
            println(it.key)
        }
        return rooms[id]
    }
}