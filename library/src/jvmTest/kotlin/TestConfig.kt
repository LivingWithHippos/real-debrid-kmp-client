package com.github.livingwithhippos.realdebrid

import java.io.File
import java.io.FileInputStream
import java.util.Properties

object TestConfig {
    private val props: Properties by lazy {
        Properties().also { props ->
            val file = File("../local.properties")
            if (file.exists()) {
                props.load(FileInputStream(file))
            }
        }
    }

    private fun get(key: String): String = props.getProperty(key) ?: error("$key missing from local.properties")

    val bearerToken: String get() = get("test.bearerToken")

    val torrentId: String get() = get("test.torrentId")

    val magnetLink: String get() = get("test.magnetLink")

    val torrentFilePath: String get() = get("test.torrentFilePath")

    val downloadId: String get() = get("test.downloadId")

    val hosterLink: String get() = get("test.hosterLink")
}
