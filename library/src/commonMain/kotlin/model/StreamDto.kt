package com.github.livingwithhippos.realdebrid.model

import kotlinx.serialization.Serializable

/**
 * Streaming links for a download. Each format maps a quality name (for example "full") to a link.
 *
 * @property apple M3U8 Live Streaming format
 * @property dash MPD Live Streaming format
 * @property liveMP4 Live MP4
 * @property h264WebM Live H264 WebM
 */
@Serializable
data class StreamDto(
    val apple: Map<String, String> = emptyMap(),
    val dash: Map<String, String> = emptyMap(),
    val liveMP4: Map<String, String> = emptyMap(),
    val h264WebM: Map<String, String> = emptyMap(),
)
