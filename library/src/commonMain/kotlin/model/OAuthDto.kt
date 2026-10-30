package com.github.livingwithhippos.realdebrid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from the endpoint that starts the device authentication process.
 * Show [userCode] and [verificationUrl] to the user, then poll every [interval] seconds.
 */
@Serializable
data class DeviceCodeDto(
    @SerialName("device_code") val deviceCode: String,
    @SerialName("user_code") val userCode: String,
    val interval: Int,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("verification_url") val verificationUrl: String,
    @SerialName("direct_verification_url") val directVerificationUrl: String? = null,
)

/**
 * User-bound credentials returned by the device credentials endpoint (open source apps flow).
 * Store them, they are needed to get and refresh the token.
 */
@Serializable
data class CredentialsDto(
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String,
)

/**
 * Response from the token endpoint, used both for the first token and for refreshing it.
 */
@Serializable
data class TokenDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("token_type") val tokenType: String,
    @SerialName("refresh_token") val refreshToken: String,
)
