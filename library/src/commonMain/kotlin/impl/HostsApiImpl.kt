package com.github.livingwithhippos.realdebrid.impl

import com.github.livingwithhippos.realdebrid.api.HostsApi
import com.github.livingwithhippos.realdebrid.model.HostStatusDto
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.executeRequest
import com.github.livingwithhippos.realdebrid.network.noAuth
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher

internal class HostsApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : HostsApi {
    override suspend fun status(): ApiResult<Map<String, HostStatusDto>> =
        executeRequest(dispatcher) {
            client.get("hosts/status")
        }

    override suspend fun regex(): ApiResult<List<String>> =
        executeRequest(dispatcher) {
            client.get("hosts/regex") { noAuth() }
        }

    override suspend fun regexFolder(): ApiResult<List<String>> =
        executeRequest(dispatcher) {
            client.get("hosts/regexFolder") { noAuth() }
        }
}
