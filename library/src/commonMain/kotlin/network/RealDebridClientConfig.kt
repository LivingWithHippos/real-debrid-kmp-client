package com.github.livingwithhippos.realdebrid.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A basic configuration for the Real-Debrid client.
 * The [logLevel] can be none (default), info, headers, body and all.
 * The token may be printed with some of these levels (for example in OAuth request bodies),
 * so only change them for debugging.
 * The [dispatcher] is where requests run and responses are decoded, so API calls are safe to make
 * from the main thread.
 */
data class RealDebridClientConfig(
    val requestTimeoutMs: Long = 60_000,
    val connectTimeoutMs: Long = 60_000,
    val socketTimeoutMs: Long = 60_000,
    val defaultHeaders: Map<String, String> = emptyMap(),
    val logLevel: String = "none",
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
)

/**
 * Class used to separate the auth config from the client config.
 * [bearerToken] can be either a private API token or an OAuth2 access token.
 */
data class RealDebridAuthState(
    var bearerToken: String = "",
)
