package com.cloutgrid.androidapp.data.network

enum class ApiConfig(val baseURL: String, val socketURL: String) {
    DEVELOPMENT(
        baseURL = "http://192.168.0.232:8000",
        socketURL = "ws://192.168.0.232:8000/ws"
    ),
    PRODUCTION(
        baseURL = "https://api.cloutgrid.com",
        socketURL = "wss://api.cloutgrid.com/ws"
    );

    companion object {
        val current: ApiConfig = DEVELOPMENT
    }
}