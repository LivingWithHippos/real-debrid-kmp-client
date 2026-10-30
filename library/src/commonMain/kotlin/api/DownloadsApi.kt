package com.github.livingwithhippos.realdebrid.api

import com.github.livingwithhippos.realdebrid.model.DownloadDto
import com.github.livingwithhippos.realdebrid.model.PagedResponseDto
import com.github.livingwithhippos.realdebrid.network.ApiResult

interface DownloadsApi {
    /**
     * Get the user's downloads list.
     * You can not use both offset and page at the same time, page is prioritized in case it happens.
     *
     * [online documentation](https://api.real-debrid.com/#Downloads)
     *
     * @param offset Starting offset (must be within 0 and [PagedResponseDto.totalCount])
     * @param page Page number
     * @param limit Entries returned per page / request (must be within 0 and 5000, default: 100)
     */
    suspend fun list(
        offset: Int? = null,
        page: Int? = null,
        limit: Int? = null,
    ): ApiResult<PagedResponseDto<DownloadDto>>

    /**
     * Delete a link from the downloads list. Returns nothing on success.
     *
     * [online documentation](https://api.real-debrid.com/#api_methods)
     *
     * @param id the download id, from [list]
     */
    suspend fun delete(id: String): ApiResult<Unit>
}
