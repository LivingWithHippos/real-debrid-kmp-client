package com.github.livingwithhippos.realdebrid.api

import com.github.livingwithhippos.realdebrid.DEVICE_GRANT_TYPE
import com.github.livingwithhippos.realdebrid.OPEN_SOURCE_CLIENT_ID
import com.github.livingwithhippos.realdebrid.model.CredentialsDto
import com.github.livingwithhippos.realdebrid.model.DeviceCodeDto
import com.github.livingwithhippos.realdebrid.model.TokenDto
import com.github.livingwithhippos.realdebrid.network.ApiResult

/**
 * OAuth2 endpoints. These calls never send the bearer token.
 *
 * Open source apps flow, the default for these functions:
 * 1. [deviceCode] to get a user code, show it to the user with the verification url
 * 2. poll [credentials] every [DeviceCodeDto.interval] seconds until the user authorizes the app,
 *    you'll receive a user-bound client id and secret
 * 3. call [token] with those credentials and the device code
 * 4. pass [TokenDto.accessToken] to the client with `updateBearerToken`
 * 5. when the token expires, call [refreshToken] and update it again
 *
 * [online documentation](https://api.real-debrid.com/#device_auth_no_secret)
 */
interface OAuthApi {
    /**
     * Starts the device authentication process.
     *
     * [online documentation](https://api.real-debrid.com/#device_auth)
     *
     * @param clientId your app client id, defaults to the open source one
     * @param newCredentials true to receive user-bound credentials, needed for open source apps
     */
    suspend fun deviceCode(
        clientId: String = OPEN_SOURCE_CLIENT_ID,
        newCredentials: Boolean = true,
    ): ApiResult<DeviceCodeDto>

    /**
     * Get the user-bound client id and secret. Returns an error until the user authorizes the app.
     *
     * [online documentation](https://api.real-debrid.com/#device_auth_no_secret)
     *
     * @param deviceCode [DeviceCodeDto.deviceCode]
     */
    suspend fun credentials(
        deviceCode: String,
        clientId: String = OPEN_SOURCE_CLIENT_ID,
    ): ApiResult<CredentialsDto>

    /**
     * Get the access token.
     *
     * [online documentation](https://api.real-debrid.com/#device_auth)
     *
     * @param clientId the client id, for open source apps the one from [credentials]
     * @param clientSecret the client secret, for open source apps the one from [credentials]
     * @param code [DeviceCodeDto.deviceCode]
     */
    suspend fun token(
        clientId: String,
        clientSecret: String,
        code: String,
        grantType: String = DEVICE_GRANT_TYPE,
    ): ApiResult<TokenDto>

    /**
     * Get a new access token using the refresh token.
     *
     * [online documentation](https://api.real-debrid.com/#device_auth_refresh)
     *
     * @param refreshToken [TokenDto.refreshToken]
     */
    suspend fun refreshToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String,
    ): ApiResult<TokenDto>
}
