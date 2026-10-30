package com.github.livingwithhippos.realdebrid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Torrent item: this class is used for both the /torrents and /torrents/info/{id} endpoint even if
 * the returned items are different. For example the info version returns the original file size and
 * the files list, while the /torrents one does not.
 *
 * @property hash SHA1 Hash of the torrent
 * @property bytes Size of selected files only
 * @property originalBytes Total size of the torrent, only in info
 * @property host Host main domain
 * @property split Split size of links
 * @property progress Possible values: 0 to 100
 * @property status Current status of the torrent: magnet_error, magnet_conversion,
 *  waiting_files_selection, queued, downloading, downloaded, error, virus, compressing, uploading, dead
 * @property added jsonDate
 * @property files only in info
 * @property links Host URLs, to be unrestricted
 * @property ended jsonDate, only present when finished
 * @property speed only present in "downloading", "compressing", "uploading" status
 * @property seeders only present in "downloading", "magnet_conversion" status
 */
@Serializable
data class TorrentDto(
    val id: String,
    val filename: String,
    @SerialName("original_filename") val originalFilename: String? = null,
    val hash: String,
    val bytes: Long,
    @SerialName("original_bytes") val originalBytes: Long? = null,
    val host: String,
    val split: Int,
    val progress: Float,
    val status: String,
    val added: String,
    val files: List<TorrentFileDto> = emptyList(),
    val links: List<String> = emptyList(),
    val ended: String? = null,
    val speed: Long? = null,
    val seeders: Int? = null,
)

/**
 * @property path Path to the file inside the torrent, starting with "/"
 * @property selected 0 or 1
 */
@Serializable
data class TorrentFileDto(
    val id: Int,
    val path: String,
    val bytes: Long,
    val selected: Int,
)

/** Returned when a torrent or magnet is added */
@Serializable
data class AddedTorrentDto(
    val id: String,
    val uri: String,
)

/**
 * @property maxFileSize Max split size possible on this host, in GB
 */
@Serializable
data class AvailableHostDto(
    val host: String,
    @SerialName("max_file_size") val maxFileSize: Long,
)
