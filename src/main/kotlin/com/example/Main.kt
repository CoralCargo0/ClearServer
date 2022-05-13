package com.example

import com.sun.net.httpserver.HttpServer
import com.example.services.MainResource
import com.example.services.ParameterFilter
import java.net.InetSocketAddress
import java.util.concurrent.Executors


fun main(args: Array<String>) {
    println("Server is started!")
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 8081), 0)
    val serverContext = server.createContext(
        "/",
        MainResource(UsersDataSource())
    )
    //serverContext.filters.add(ParameterFilter())
    serverContext.filters.add(ParameterFilter())
    val executor = Executors.newCachedThreadPool()
    server.executor = executor
    server.start()
}