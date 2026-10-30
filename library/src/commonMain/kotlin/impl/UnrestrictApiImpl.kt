package com.github.livingwithhippos.realdebrid.impl

import com.github.livingwithhippos.realdebrid.api.UnrestrictApi
import com.github.livingwithhippos.realdebrid.model.DownloadDto
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.executeRequest
import com.github.livingwithhippos.realdebrid.network.requireNotBlank
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineDispatcher

internal class UnrestrictApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : UnrestrictApi {
    override suspend fun link(
        link: String,
        password: String?,
        remote: Boolean?,
    ): ApiResult<DownloadDto> {
        requireNotBlank(link, "link")?.let { return it }
        return executeRequest(dispatcher) {
            client.submitForm(
                url = "unrestrict/link",
                formParameters =
                    parameters {
                        append("link", link)
                        password?.let { append("password", it) }
                        remote?.let { append("remote", if (it) "1" else "0") }
                    },
            )
        }
    }

    override suspend fun folder(link: String): ApiResult<List<String>> {
        requireNotBlank(link, "link")?.let { return it }
        return executeRequest(dispatcher) {
            client.submitForm(
                url = "unrestrict/folder",
                formParameters = parameters { append("link", link) },
            )
        }
    }

    override suspend fun containerFile(fileBytes: ByteArray): ApiResult<List<String>> {
        if (fileBytes.isEmpty()) {
            return ApiResult.Failure.BadRequest(IllegalArgumentException("fileBytes must not be empty"))
        }
        return executeRequest(dispatcher) {
            client.put("unrestrict/containerFile") {
                setBody(ByteArrayContent(fileBytes, ContentType.Application.OctetStream))
            }
        }
    }

    override suspend fun containerLink(link: String): ApiResult<List<String>> {
        requireNotBlank(link, "link")?.let { return it }
        return executeRequest(dispatcher) {
            client.submitForm(
                url = "unrestrict/containerLink",
                formParameters = parameters { append("link", link) },
            )
        }
    }
}
