package com.github.livingwithhippos.realdebrid.impl

import com.github.livingwithhippos.realdebrid.api.UserApi
import com.github.livingwithhippos.realdebrid.model.UserDto
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.executeEmptyRequest
import com.github.livingwithhippos.realdebrid.network.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher

internal class UserApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : UserApi {
    override suspend fun get(): ApiResult<UserDto> =
        executeRequest(dispatcher) {
            client.get("user")
        }

    override suspend fun disableAccessToken(): ApiResult<Unit> =
        executeEmptyRequest(dispatcher) {
            client.get("disable_access_token")
        }
}
