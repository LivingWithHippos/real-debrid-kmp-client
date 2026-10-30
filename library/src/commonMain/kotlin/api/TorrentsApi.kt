package com.github.livingwithhippos.realdebrid.api

import com.github.livingwithhippos.realdebrid.model.AddedTorrentDto
import com.github.livingwithhippos.realdebrid.model.AvailableHostDto
import com.github.livingwithhippos.realdebrid.model.PagedResponseDto
import com.github.livingwithhippos.realdebrid.model.TorrentDto
import com.github.livingwithhippos.realdebrid.network.ApiResult

interface TorrentsApi {
    /**
     * Get the user's torrents list.
     * You can not use both offset and page at the same time, page is prioritized in case it happens.
     *
     * [online documentation](https://api.real-debrid.com/#Torrents)
     *
     * @param offset Starting offset (must be within 0 and [PagedResponseDto.totalCount])
     * @param page Page number
     * @param limit Entries returned per page / request (must be within 0 and 5000, default: 100)
     * @param filter "active" to list active torrents only
     */
    suspend fun list(
        offset: Int? = null,
        page: Int? = null,
        limit: Int? = null,
        filter: String? = null,
    ): ApiResult<PagedResponseDto<TorrentDto>>

    /**
     * Get all information on the asked torrent, including the files list.
     *
     * [online documentation](https://api.real-debrid.com/#TorrentsId)
     */
    suspend fun info(id: String): ApiResult<TorrentDto>

    /**
     * Get available hosts to upload the torrent to.
     *
     * [online documentation](https://api.real-debrid.com/#TorrentsAvailableHosts)
     */
    suspend fun availableHosts(): ApiResult<List<AvailableHostDto>>

    /**
     * Add a torrent file to download. Remember to call [selectFiles] to start it.
     *
     * [online documentation](https://api.real-debrid.com/#TorrentsTorrent)
     *
     * @param fileBytes the torrent file
     * @param host Hoster domain, from [availableHosts]
     */
    suspend fun addTorrent(
        fileBytes: ByteArray,
        host: String? = null,
    ): ApiResult<AddedTorrentDto>

    /**
     * Add a magnet link to download. Remember to call [selectFiles] to start it.
     *
     * [online documentation](https://api.real-debrid.com/#TorrentsMagnet)
     *
     * @param host Hoster domain, from [availableHosts]
     */
    suspend fun addMagnet(
        magnet: String,
        host: String? = null,
    ): ApiResult<AddedTorrentDto>

    /**
     * Select files of a torrent to start it. Returns nothing on success.
     *
     * [online documentation](https://api.real-debrid.com/#api_methods)
     *
     * @param id the torrent id
     * @param fileIds the ids of the files to download, from [info]. Null selects all files.
     */
    suspend fun selectFiles(
        id: String,
        fileIds: List<Int>? = null,
    ): ApiResult<Unit>

    /**
     * Delete a torrent from the torrents list. Returns nothing on success.
     *
     * [online documentation](https://api.real-debrid.com/#api_methods)
     */
    suspend fun delete(id: String): ApiResult<Unit>
}
