package com.github.livingwithhippos.realdebrid

/** Base url for the REST APIs */
internal const val BASE_URL: String = "https://api.real-debrid.com/rest/1.0/"

/** Base url for the OAuth2 APIs */
internal const val OAUTH_URL: String = "https://api.real-debrid.com/oauth/v2/"

/**
 * Client id that open source apps can use for the device authentication flow.
 * It is limited to the unrestrict, torrents, downloads and user scopes.
 */
const val OPEN_SOURCE_CLIENT_ID: String = "X245A4XAIBGVM"

/** Grant type used for the device authentication flow and to refresh a token */
const val DEVICE_GRANT_TYPE: String = "http://oauth.net/grant_type/device/1.0"
