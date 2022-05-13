package com.example

import com.sun.net.httpserver.HttpServer
import com.example.services.MainResource
import com.example.services.ParameterFilter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.Executors


fun main(args: Array<String>) {
    println("Server is started!")
    InetAddress.getByName("")
    val server = HttpServer.create(InetSocketAddress(InetAddress.getByName("clearserver-production.up.railway.app"), 8080), 0)
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