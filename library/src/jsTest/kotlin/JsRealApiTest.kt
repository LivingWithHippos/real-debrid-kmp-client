package com.github.livingwithhippos.realdebrid

import com.github.livingwithhippos.realdebrid.client.RealDebridClient
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.RealDebridAuthState
import com.github.livingwithhippos.realdebrid.network.RealDebridClientConfig
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The @Test annotation has been placed only on calls not needing a token.
 * Additional tests mirror JvmRealApiTest. Populate mise.local.toml or local.properties and add @Test
 * to test specific real responses on Kotlin/JS.
 */
class JsRealApiTest {
    private val client by lazy {
        RealDebridClient.create(
            RealDebridClientConfig(logLevel = "info"),
            RealDebridAuthState(JsTestConfig.bearerToken),
        )
    }

    @Test
    fun hostsRegex() =
        runTest {
            val response = RealDebridClient.create(RealDebridClientConfig()).hosts.regex()
            assertTrue { response is ApiResult.Success && response.value.isNotEmpty() }
        }

    fun userGet() =
        runTest {
            val response = client.user.get()
            assertTrue { response is ApiResult.Success }
        }

    fun torrentsList() =
        runTest {
            val response = client.torrents.list(limit = 2)
            assertTrue { response is ApiResult.Success }
        }

    fun downloadsList() =
        runTest {
            val response = client.downloads.list(limit = 2)
            assertTrue { response is ApiResult.Success }
        }

    fun unrestrictLink() =
        runTest {
            val response = client.unrestrict.link(JsTestConfig.hosterLink)
            assertTrue { response is ApiResult.Success }
        }

    fun torrentsAddMagnet() =
        runTest {
            val response = client.torrents.addMagnet(JsTestConfig.magnetLink)
            assertTrue { response is ApiResult.Success }
        }

    fun streamingTranscode() =
        runTest {
            val response = client.streaming.transcode(JsTestConfig.downloadId)
            assertTrue { response is ApiResult.Success }
        }
}
