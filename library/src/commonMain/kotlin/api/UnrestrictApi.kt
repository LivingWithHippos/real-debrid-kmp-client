package com.github.livingwithhippos.realdebrid.api

import com.github.livingwithhippos.realdebrid.model.DownloadDto
import com.github.livingwithhippos.realdebrid.network.ApiResult

interface UnrestrictApi {
    /**
     * Unrestrict a hoster link and get a new download link.
     *
     * [online documentation](https://api.real-debrid.com/#UnrestrictLinkOne)
     *
     * @param link the original hoster link
     * @param password password to unlock the file access hoster side
     * @param remote true to use Remote traffic, dedicated servers and account sharing protections lifted
     */
    suspend fun link(
        link: String,
        password: String? = null,
        remote: Boolean? = null,
    ): ApiResult<DownloadDto>

    /**
     * Unrestrict a hoster folder link and get individual links.
     * Returns an empty list if no links are found.
     *
     * [online documentation](https://api.real-debrid.com/#UnrestrictFolder)
     *
     * @return the list of links in the folder, not unrestricted
     */
    suspend fun folder(link: String): ApiResult<List<String>>

    /**
     * Decrypt a container file (RSDF, CCF, CCF3, DLC).
     *
     * [online documentation](https://api.real-debrid.com/#UnrestrictContainerDecrypt)
     *
     * @param fileBytes the container file
     * @return the list of links in the container, not unrestricted
     */
    suspend fun containerFile(fileBytes: ByteArray): ApiResult<List<String>>

    /**
     * Decrypt a container file from a link.
     *
     * [online documentation](https://api.real-debrid.com/#UnrestrictContainerDecrypt)
     *
     * @param link HTTP Link of the container file
     * @return the list of links in the container, not unrestricted
     */
    suspend fun containerLink(link: String): ApiResult<List<String>>
}
