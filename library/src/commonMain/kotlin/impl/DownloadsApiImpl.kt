package com.github.livingwithhippos.realdebrid.impl

import com.github.livingwithhippos.realdebrid.api.DownloadsApi
import com.github.livingwithhippos.realdebrid.model.DownloadDto
import com.github.livingwithhippos.realdebrid.model.PagedResponseDto
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.executeEmptyRequest
import com.github.livingwithhippos.realdebrid.network.executePagedRequest
import com.github.livingwithhippos.realdebrid.network.requireNotBlank
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.encodeURLPathPart
import kotlinx.coroutines.CoroutineDispatcher

internal class DownloadsApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : DownloadsApi {
    override suspend fun list(
        offset: Int?,
        page: Int?,
        limit: Int?,
    ): ApiResult<PagedResponseDto<DownloadDto>> =
        executePagedRequest(dispatcher) {
            client.get("downloads") {
                parameter("offset", offset)
                parameter("page", page)
                parameter("limit", limit)
            }
        }

    override suspend fun delete(id: String): ApiResult<Unit> {
        requireNotBlank(id, "id")?.let { return it }
        return executeEmptyRequest(dispatcher) {
            client.delete("downloads/delete/${id.encodeURLPathPart()}")
        }
    }
}
