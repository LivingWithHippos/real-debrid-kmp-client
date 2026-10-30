package com.github.livingwithhippos.realdebrid.impl

import com.github.livingwithhippos.realdebrid.api.StreamingApi
import com.github.livingwithhippos.realdebrid.model.StreamDto
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.executeRequest
import com.github.livingwithhippos.realdebrid.network.requireNotBlank
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.encodeURLPathPart
import kotlinx.coroutines.CoroutineDispatcher

internal class StreamingApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : StreamingApi {
    override suspend fun transcode(id: String): ApiResult<StreamDto> {
        requireNotBlank(id, "id")?.let { return it }
        return executeRequest(dispatcher) {
            client.get("streaming/transcode/${id.encodeURLPathPart()}")
        }
    }
}
