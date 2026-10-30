package com.github.livingwithhippos.realdebrid

import com.github.livingwithhippos.realdebrid.impl.TorrentsApiImpl
import com.github.livingwithhippos.realdebrid.network.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertTrue

/** Checks that responses are decoded on the configured dispatcher, not the caller's thread. */
class DispatcherTest {
    @Test
    fun `response is decoded on the configured dispatcher`() {
        val decodeDispatcher =
            Executors
                .newSingleThreadExecutor { Thread(it, "realdebrid-decode") }
                .asCoroutineDispatcher()
        val engine =
            MockEngine {
                respond(
                    content = "[]",
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val client = HttpClient(engine) { install(ContentNegotiation) { json() } }

        var decodeThread: String? = null
        // runs where the body is transformed (i.e. where JSON decoding happens)
        client.responsePipeline.intercept(HttpResponsePipeline.Transform) {
            decodeThread = Thread.currentThread().name
            proceed()
        }

        decodeDispatcher.use { dispatcher ->
            val callerThread = Thread.currentThread().name
            val result = runBlocking { TorrentsApiImpl(client, dispatcher).list() }

            assertTrue(result is ApiResult.Success)
            // coroutine debug mode may append " @coroutine#N" to the thread name
            assertTrue(decodeThread!!.startsWith("realdebrid-decode"), "decoded on $decodeThread")
            assertTrue(!decodeThread.startsWith(callerThread))
        }
    }
}
