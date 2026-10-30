package com.github.livingwithhippos.realdebrid.impl

import com.github.livingwithhippos.realdebrid.api.TorrentsApi
import com.github.livingwithhippos.realdebrid.model.AddedTorrentDto
import com.github.livingwithhippos.realdebrid.model.AvailableHostDto
import com.github.livingwithhippos.realdebrid.model.PagedResponseDto
import com.github.livingwithhippos.realdebrid.model.TorrentDto
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.executeEmptyRequest
import com.github.livingwithhippos.realdebrid.network.executePagedRequest
import com.github.livingwithhippos.realdebrid.network.executeRequest
import com.github.livingwithhippos.realdebrid.network.requireNotBlank
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.encodeURLPathPart
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineDispatcher

internal class TorrentsApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : TorrentsApi {
    override suspend fun list(
        offset: Int?,
        page: Int?,
        limit: Int?,
        filter: String?,
    ): ApiResult<PagedResponseDto<TorrentDto>> =
        executePagedRequest(dispatcher) {
            client.get("torrents") {
                parameter("offset", offset)
                parameter("page", page)
                parameter("limit", limit)
                parameter("filter", filter)
            }
        }

    override suspend fun info(id: String): ApiResult<TorrentDto> {
        requireNotBlank(id, "id")?.let { return it }
        return executeRequest(dispatcher) {
            client.get("torrents/info/${id.encodeURLPathPart()}")
        }
    }

    override suspend fun availableHosts(): ApiResult<List<AvailableHostDto>> =
        executeRequest(dispatcher) {
            client.get("torrents/availableHosts")
        }

    override suspend fun addTorrent(
        fileBytes: ByteArray,
        host: String?,
    ): ApiResult<AddedTorrentDto> {
        if (fileBytes.isEmpty()) {
            return ApiResult.Failure.BadRequest(IllegalArgumentException("fileBytes must not be empty"))
        }
        return executeRequest(dispatcher) {
            client.put("torrents/addTorrent") {
                parameter("host", host)
                setBody(ByteArrayContent(fileBytes, ContentType("application", "x-bittorrent")))
            }
        }
    }

    override suspend fun addMagnet(
        magnet: String,
        host: String?,
    ): ApiResult<AddedTorrentDto> {
        requireNotBlank(magnet, "magnet")?.let { return it }
        return executeRequest(dispatcher) {
            client.submitForm(
                url = "torrents/addMagnet",
                formParameters =
                    parameters {
                        append("magnet", magnet)
                        host?.let { append("host", it) }
                    },
            )
        }
    }

    override suspend fun selectFiles(
        id: String,
        fileIds: List<Int>?,
    ): ApiResult<Unit> {
        requireNotBlank(id, "id")?.let { return it }
        if (fileIds != null && fileIds.isEmpty()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("fileIds must not be empty, use null to select all the files"),
            )
        }
        return executeEmptyRequest(dispatcher) {
            client.submitForm(
                url = "torrents/selectFiles/${id.encodeURLPathPart()}",
                formParameters = parameters { append("files", fileIds?.joinToString(",") ?: "all") },
            )
        }
    }

    override suspend fun delete(id: String): ApiResult<Unit> {
        requireNotBlank(id, "id")?.let { return it }
        return executeEmptyRequest(dispatcher) {
            client.delete("torrents/delete/${id.encodeURLPathPart()}")
        }
    }
}
