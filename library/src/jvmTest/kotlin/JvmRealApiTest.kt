package com.github.livingwithhippos.realdebrid

import com.github.livingwithhippos.realdebrid.client.RealDebridClient
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.RealDebridAuthState
import com.github.livingwithhippos.realdebrid.network.RealDebridClientConfig
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The @Test has been removed from the functions needing a token to avoid triggering them with the Gradle tasks,
 * populate the local properties file like the example and readd them to test real responses
 */
class JvmRealApiTest {
    private val client by lazy {
        RealDebridClient.create(RealDebridClientConfig(logLevel = "body"), RealDebridAuthState(TestConfig.bearerToken))
    }

    /*********
     * OAUTH *
     ********/

    fun `oauth device code`() =
        runBlocking {
            val response = RealDebridClient.create(RealDebridClientConfig()).oauth.deviceCode()
            assertTrue { response is ApiResult.Success }
        }

    /********
     * USER *
     *******/

    @Test
    fun `user get`() =
        runBlocking {
            val response = client.user.get()
            assertTrue { response is ApiResult.Success }
        }

    /**************
     * UNRESTRICT *
     *************/

    fun `unrestrict link`() =
        runBlocking {
            val response = client.unrestrict.link(TestConfig.hosterLink)
            assertTrue { response is ApiResult.Success }
        }

    /*************
     * DOWNLOADS *
     ************/

    @Test
    fun `downloads list`() =
        runBlocking {
            val response = client.downloads.list(limit = 10)
            assertTrue { response is ApiResult.Success }
        }

    /************
     * TORRENTS *
     ***********/

    fun `torrents list`() =
        runBlocking {
            val response = client.torrents.list(limit = 2)
            assertTrue { response is ApiResult.Success }
        }

    fun `torrents info`() =
        runBlocking {
            val response = client.torrents.info(TestConfig.torrentId)
            assertTrue { response is ApiResult.Success }
        }

    fun `torrents available hosts`() =
        runBlocking {
            val response = client.torrents.availableHosts()
            assertTrue { response is ApiResult.Success }
        }

    fun `torrents add magnet`() =
        runBlocking {
            val response = client.torrents.addMagnet(TestConfig.magnetLink)
            assertTrue { response is ApiResult.Success }
        }

    fun `torrents add torrent`() =
        runBlocking {
            val response = client.torrents.addTorrent(File(TestConfig.torrentFilePath).readBytes())
            assertTrue { response is ApiResult.Success }
        }

    fun `torrents select files`() =
        runBlocking {
            val response = client.torrents.selectFiles(TestConfig.torrentId)
            assertTrue { response is ApiResult.Success }
        }

    /*********
     * HOSTS *
     ********/

    fun `hosts status`() =
        runBlocking {
            val response = client.hosts.status()
            assertTrue { response is ApiResult.Success }
        }

    @Test
    fun `hosts regex`() =
        runBlocking {
            val response = RealDebridClient.create(RealDebridClientConfig()).hosts.regex()
            assertTrue { response is ApiResult.Success && response.value.isNotEmpty() }
        }

    /*************
     * STREAMING *
     ************/

    fun `streaming transcode`() =
        runBlocking {
            val response = client.streaming.transcode(TestConfig.downloadId)
            assertTrue { response is ApiResult.Success }
        }
}
