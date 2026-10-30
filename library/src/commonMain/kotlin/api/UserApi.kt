package com.github.livingwithhippos.realdebrid.api

import com.github.livingwithhippos.realdebrid.model.UserDto
import com.github.livingwithhippos.realdebrid.network.ApiResult

interface UserApi {
    /**
     * Returns some information on the current user.
     *
     * [online documentation](https://api.real-debrid.com/#User)
     */
    suspend fun get(): ApiResult<UserDto>

    /**
     * Disables the current access token. Returns nothing on success.
     *
     * [online documentation](https://api.real-debrid.com/#api_methods)
     */
    suspend fun disableAccessToken(): ApiResult<Unit>
}
