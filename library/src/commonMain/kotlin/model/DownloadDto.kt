package com.github.livingwithhippos.realdebrid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A download, returned by the downloads list and by the unrestrict link endpoint.
 *
 * @property mimeType Mime Type of the file, guessed by the file extension
 * @property fileSize bytes, 0 if unknown
 * @property link Original link, use [download] to download this file
 * @property host Host main domain
 * @property chunks Max Chunks allowed
 * @property crc Disable / enable CRC check
 * @property download Generated link to be used for downloading
 * @property streamable Is the file streamable on website (0 or 1)
 * @property generated jsonDate
 * @property type Type of the file (in general, its quality)
 */
@Serializable
data class DownloadDto(
    val id: String,
    val filename: String,
    val mimeType: String? = null,
    @SerialName("filesize") val fileSize: Long = 0,
    val link: String,
    val host: String,
    @SerialName("host_icon") val hostIcon: String? = null,
    val chunks: Int = 0,
    val crc: Int? = null,
    val download: String,
    val streamable: Int? = null,
    val generated: String? = null,
    val type: String? = null,
    val alternative: List<AlternativeDto> = emptyList(),
)

@Serializable
data class AlternativeDto(
    val id: String,
    val filename: String,
    val download: String,
    val mimeType: String? = null,
    val quality: String? = null,
)
