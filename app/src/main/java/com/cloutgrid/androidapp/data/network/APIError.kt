package com.cloutgrid.androidapp.data.network

sealed class APIError : Exception() {
    class InvalidURL : APIError() {
        private fun readResolve(): Any = InvalidURL()
    }

    data class ServerError(override val message: String) : APIError()
    data class DecodingError(override val message: String) : APIError()
}