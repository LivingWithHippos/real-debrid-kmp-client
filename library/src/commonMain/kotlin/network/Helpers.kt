package com.github.livingwithhippos.realdebrid.network

import com.github.livingwithhippos.realdebrid.model.PagedResponseDto
import com.github.livingwithhippos.realdebrid.model.RealDebridErrorDto
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

internal const val TOTAL_COUNT_HEADER = "X-Total-Count"

internal suspend fun HttpResponse.safeBodyText(): String? = runCatching { bodyAsText() }.getOrNull()

internal fun HttpStatusCode.isSuccessful(): Boolean = value in 200..299

internal fun toFailure(
    throwable: Throwable,
    body: String?,
): ApiResult.Failure =
    when (throwable) {
        is ResponseException -> {
            ApiResult.Failure.Http(
                throwable.response.status.value,
                body,
            )
        }

        // Ktor wraps decoding errors in a ContentConvertException
        is SerializationException, is ContentConvertException -> {
            ApiResult.Failure.Serialization(throwable, body)
        }

        else -> {
            ApiResult.Failure.Network(throwable)
        }
    }

/**
 * Real-Debrid errors are sent with a 4XX/5XX code and a body like
 * `{"error": "bad_token", "error_code": 8}`, this parses it if possible.
 */
internal suspend fun HttpResponse.toErrorFailure(): ApiResult.Failure {
    val body = safeBodyText()
    val error =
        body?.let {
            runCatching { RealDebridJson.decodeFromString<RealDebridErrorDto>(it) }.getOrNull()
        }
    return if (error != null) {
        ApiResult.Failure.Api(status.value, error)
    } else {
        ApiResult.Failure.Http(status.value, body)
    }
}

/**
 * Runs [request] on [dispatcher] and turns a successful response into a value with [parse].
 * Errors and exceptions are returned as [ApiResult.Failure], cancellation is rethrown.
 */
internal suspend fun <R> executeRaw(
    dispatcher: CoroutineDispatcher,
    request: suspend () -> HttpResponse,
    parse: suspend (HttpResponse) -> R,
): ApiResult<R> =
    withContext(dispatcher) {
        val response =
            try {
                request()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                return@withContext toFailure(e, null)
            }

        if (!response.status.isSuccessful()) {
            return@withContext response.toErrorFailure()
        }

        try {
            ApiResult.Success(parse(response))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            toFailure(e, response.safeBodyText())
        }
    }

/** For endpoints returning a JSON body */
internal suspend inline fun <reified T> executeRequest(
    dispatcher: CoroutineDispatcher,
    noinline request: suspend () -> HttpResponse,
): ApiResult<T> = executeRaw(dispatcher, request) { it.body<T>() }

/** For endpoints returning 204 No Content, or whose body is not needed */
internal suspend fun executeEmptyRequest(
    dispatcher: CoroutineDispatcher,
    request: suspend () -> HttpResponse,
): ApiResult<Unit> = executeRaw(dispatcher, request) { }

/**
 * For paginated list endpoints. The total count is read from the X-Total-Count header.
 * Real-Debrid returns 204 No Content instead of an empty list when there are no items.
 */
internal suspend inline fun <reified T> executePagedRequest(
    dispatcher: CoroutineDispatcher,
    noinline request: suspend () -> HttpResponse,
): ApiResult<PagedResponseDto<T>> =
    executeRaw(dispatcher, request) { response ->
        val items =
            if (response.status == HttpStatusCode.NoContent) {
                emptyList()
            } else {
                response.body<List<T>>()
            }
        PagedResponseDto(
            items = items,
            totalCount = response.headers[TOTAL_COUNT_HEADER]?.toIntOrNull(),
        )
    }

/** Returns a [ApiResult.Failure.BadRequest] if [value] is blank */
internal fun requireNotBlank(
    value: String,
    name: String,
): ApiResult.Failure.BadRequest? =
    if (value.isBlank()) {
        ApiResult.Failure.BadRequest(IllegalArgumentException("$name must not be blank"))
    } else {
        null
    }
