package com.github.livingwithhippos.realdebrid.impl

import com.github.livingwithhippos.realdebrid.DEVICE_GRANT_TYPE
import com.github.livingwithhippos.realdebrid.OAUTH_URL
import com.github.livingwithhippos.realdebrid.api.OAuthApi
import com.github.livingwithhippos.realdebrid.model.CredentialsDto
import com.github.livingwithhippos.realdebrid.model.DeviceCodeDto
import com.github.livingwithhippos.realdebrid.model.TokenDto
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.executeRequest
import com.github.livingwithhippos.realdebrid.network.noAuth
import com.github.livingwithhippos.realdebrid.network.requireNotBlank
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineDispatcher

internal class OAuthApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : OAuthApi {
    override suspend fun deviceCode(
        clientId: String,
        newCredentials: Boolean,
    ): ApiResult<DeviceCodeDto> =
        executeRequest(dispatcher) {
            client.get("${OAUTH_URL}device/code") {
                noAuth()
                parameter("client_id", clientId)
                if (newCredentials) parameter("new_credentials", "yes")
            }
        }

    override suspend fun credentials(
        deviceCode: String,
        clientId: String,
    ): ApiResult<CredentialsDto> {
        requireNotBlank(deviceCode, "deviceCode")?.let { return it }
        return executeRequest(dispatcher) {
            client.get("${OAUTH_URL}device/credentials") {
                noAuth()
                parameter("client_id", clientId)
                parameter("code", deviceCode)
            }
        }
    }

    override suspend fun token(
        clientId: String,
        clientSecret: String,
        code: String,
        grantType: String,
    ): ApiResult<TokenDto> {
        requireNotBlank(code, "code")?.let { return it }
        return executeRequest(dispatcher) {
            client.submitForm(
                url = "${OAUTH_URL}token",
                formParameters =
                    parameters {
                        append("client_id", clientId)
                        append("client_secret", clientSecret)
                        append("code", code)
                        append("grant_type", grantType)
                    },
            ) {
                noAuth()
            }
        }
    }

    override suspend fun refreshToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String,
    ): ApiResult<TokenDto> =
        token(
            clientId = clientId,
            clientSecret = clientSecret,
            code = refreshToken,
            grantType = DEVICE_GRANT_TYPE,
        )
}
