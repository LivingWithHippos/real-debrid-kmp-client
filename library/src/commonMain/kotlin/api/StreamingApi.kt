package com.github.livingwithhippos.realdebrid.api

import com.github.livingwithhippos.realdebrid.model.StreamDto
import com.github.livingwithhippos.realdebrid.network.ApiResult

interface StreamingApi {
    /**
     * Get transcoding links for the given file.
     * Does not work with the open source client id token.
     *
     * [online documentation](https://api.real-debrid.com/#StreamingTranscode)
     *
     * @param id the download id, from the downloads list or an unrestricted link
     */
    suspend fun transcode(id: String): ApiResult<StreamDto>
}
