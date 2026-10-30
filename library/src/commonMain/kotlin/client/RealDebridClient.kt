package com.github.livingwithhippos.realdebrid.client

import com.github.livingwithhippos.realdebrid.api.DownloadsApi
import com.github.livingwithhippos.realdebrid.api.HostsApi
import com.github.livingwithhippos.realdebrid.api.OAuthApi
import com.github.livingwithhippos.realdebrid.api.StreamingApi
import com.github.livingwithhippos.realdebrid.api.TorrentsApi
import com.github.livingwithhippos.realdebrid.api.UnrestrictApi
import com.github.livingwithhippos.realdebrid.api.UserApi
import com.github.livingwithhippos.realdebrid.impl.DownloadsApiImpl
import com.github.livingwithhippos.realdebrid.impl.HostsApiImpl
import com.github.livingwithhippos.realdebrid.impl.OAuthApiImpl
import com.github.livingwithhippos.realdebrid.impl.StreamingApiImpl
import com.github.livingwithhippos.realdebrid.impl.TorrentsApiImpl
import com.github.livingwithhippos.realdebrid.impl.UnrestrictApiImpl
import com.github.livingwithhippos.realdebrid.impl.UserApiImpl
import com.github.livingwithhippos.realdebrid.network.RealDebridAuthState
import com.github.livingwithhippos.realdebrid.network.RealDebridClientConfig
import com.github.livingwithhippos.realdebrid.network.createHttpClient
import com.github.livingwithhippos.realdebrid.network.platformEngineFactory
import io.ktor.client.HttpClient

class RealDebridClient private constructor(
    val oauth: OAuthApi,
    val user: UserApi,
    val unrestrict: UnrestrictApi,
    val downloads: DownloadsApi,
    val torrents: TorrentsApi,
    val hosts: HostsApi,
    val streaming: StreamingApi,
    private val httpClient: HttpClient,
    private val authState: RealDebridAuthState,
) {
    companion object {
        fun create(
            config: RealDebridClientConfig,
            authState: RealDebridAuthState = RealDebridAuthState(),
        ): RealDebridClient {
            val client =
                createHttpClient(config = config, authState = authState, engineFactory = platformEngineFactory())
            return RealDebridClient(
                oauth = OAuthApiImpl(client, config.dispatcher),
                user = UserApiImpl(client, config.dispatcher),
                unrestrict = UnrestrictApiImpl(client, config.dispatcher),
                downloads = DownloadsApiImpl(client, config.dispatcher),
                torrents = TorrentsApiImpl(client, config.dispatcher),
                hosts = HostsApiImpl(client, config.dispatcher),
                streaming = StreamingApiImpl(client, config.dispatcher),
                httpClient = client,
                authState = authState,
            )
        }
    }

    /** Updates the token used for the requests, either a private API token or an OAuth2 access token */
    fun updateBearerToken(token: String) {
        authState.bearerToken = token
    }

    fun close() = httpClient.close()
}
