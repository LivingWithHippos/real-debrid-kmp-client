package com.github.livingwithhippos.realdebrid.network

import com.github.livingwithhippos.realdebrid.model.RealDebridErrorDto

sealed interface ApiResult<out T> {
    data class Success<T>(
        val value: T,
    ) : ApiResult<T>

    sealed interface Failure : ApiResult<Nothing> {
        /**
         * The server returned an error with the Real-Debrid error body,
         * use [RealDebridErrorDto.code] to check which error it is.
         */
        data class Api(
            val httpCode: Int,
            val error: RealDebridErrorDto,
        ) : Failure

        /** The server returned an error without the standard Real-Debrid error body */
        data class Http(
            val code: Int,
            val body: String?,
        ) : Failure

        /** The request was not sent because its parameters were not valid */
        data class BadRequest(
            val cause: Throwable,
        ) : Failure

        data class Network(
            val cause: Throwable,
        ) : Failure

        data class Serialization(
            val cause: Throwable,
            val body: String?,
        ) : Failure
    }
}
