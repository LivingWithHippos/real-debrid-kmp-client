package com.github.livingwithhippos.realdebrid.api

import com.github.livingwithhippos.realdebrid.model.HostStatusDto
import com.github.livingwithhippos.realdebrid.network.ApiResult

interface HostsApi {
    /**
     * Get the status of the supported hosters, mapped by host domain.
     *
     * [online documentation](https://api.real-debrid.com/#HostsStatus)
     */
    suspend fun status(): ApiResult<Map<String, HostStatusDto>>

    /**
     * Get all supported regex, useful to find supported links inside a document.
     * Does not require authentication.
     *
     * [online documentation](https://api.real-debrid.com/#HostsRegex)
     */
    suspend fun regex(): ApiResult<List<String>>

    /**
     * Get all supported folder regex, useful to find supported links inside a document.
     * Does not require authentication.
     *
     * [online documentation](https://api.real-debrid.com/#HostsRegex)
     */
    suspend fun regexFolder(): ApiResult<List<String>>
}
