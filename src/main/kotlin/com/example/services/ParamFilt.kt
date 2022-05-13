package com.example.services

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


class ParameterFilter : Filter() {

    val PARAMETERS = "params"
    val LEVEL_ID = "levelId"
    val REQUEST_TYPE = "request_type"

    override fun description(): String {
        return "This parses request parameters"
    }

    @Throws(IOException::class)
    override fun doFilter(exchange: HttpExchange, chain: Chain) {
        val parameters: MutableMap<String?, Any?> = HashMap()
        parseGetParameters(exchange, parameters)
        exchange.setAttribute(PARAMETERS, parameters)
        chain.doFilter(exchange)
    }

    @Throws(UnsupportedEncodingException::class)
    private fun parseGetParameters(exchange: HttpExchange, parameters: MutableMap<String?, Any?>) {
        val requestedUri = exchange.requestURI
        val query = requestedUri.rawQuery
        parseQuery(query, parameters)
        val path = requestedUri.rawPath
        parsePath(path, parameters)
    }

    @Throws(UnsupportedEncodingException::class)
    private fun parseQuery(query: String?, parameters: MutableMap<String?, Any?>) {
        if (query != null) {
            val pairs = query.split('&').toTypedArray()
            for (pair in pairs) {
                val param = pair.split('=')
                var key: String? = null
                var value: String? = null
                if (param.isNotEmpty()) {
                    key = URLDecoder.decode(param[0], UTF_8)
                }
                if (param.size > 1) {
                    value = URLDecoder.decode(param[1], UTF_8)
                }
                parameters[key] = value
            }
        }
    }

    private fun parsePath(path: String?, parameters: MutableMap<String?, Any?>) {
        if (path != null) {
            val pathParameters = path.split("/").toTypedArray()
            if (pathParameters.size == 3) {
                parameters[LEVEL_ID] = pathParameters[1]
                parameters[REQUEST_TYPE] = pathParameters[2]
            }
        }
    }

    companion object {
        const val UTF_8 = "UTF-8"
    }
}