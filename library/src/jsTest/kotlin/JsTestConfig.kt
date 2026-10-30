package com.github.livingwithhippos.realdebrid

object JsTestConfig {
    // In Node.js environment, access process.env directly
    private val env: dynamic
        get() =
            try {
                js("process.env")
            } catch (e: Throwable) {
                js("(typeof window !== 'undefined') ? window.__ENV__ : {}")
            }

    val bearerToken: String
        get() = env.TEST_BEARER_TOKEN?.toString() ?: error("TEST_BEARER_TOKEN env var not set")

    val torrentId: String
        get() = env.TEST_TORRENT_ID?.toString() ?: error("TEST_TORRENT_ID env var not set")

    val magnetLink: String
        get() = env.TEST_MAGNET_LINK?.toString() ?: error("TEST_MAGNET_LINK env var not set")

    val downloadId: String
        get() = env.TEST_DOWNLOAD_ID?.toString() ?: error("TEST_DOWNLOAD_ID env var not set")

    val hosterLink: String
        get() = env.HOSTER_LINK?.toString() ?: error("HOSTER_LINK env var not set")
}
