package com.example

import com.sun.net.httpserver.HttpServer
import com.example.services.MainResource
import com.example.services.ParameterFilter
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    val asd = NetworkInterface.getNetworkInterfaces()
    var hostname = ""
    val port = 8082
    try {
        while (asd.hasMoreElements() && hostname.isEmpty()) {
            asd.nextElement().apply {
                if (this.name == "wlan0") {
                    hostname = inetAddresses().findFirst().get().hostAddress
                }
            }
        }
    } catch (e: Throwable) {
        println("error" + e.message)
        return
    }
    println("Server is started! IP - $hostname  Port - $port")
    val server = HttpServer.create()
    server.bind(InetSocketAddress(hostname, port), 0)
    val serverContext = server.createContext(
        "/",
        MainResource(UsersDataSource())
    )
    serverContext.filters.add(ParameterFilter())
    val executor = Executors.newCachedThreadPool()
    server.executor = executor
    server.start()
}
