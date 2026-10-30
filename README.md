# real-debrid-kmp-client

This is a Kotlin multi platform library, created to easily interact with [Real-Debrid](https://real-debrid.com). It's still in beta; until `1.0.0` breaking changes and/or bugs may occur.

It's the Real-Debrid sibling of [torbox-kmp-client](https://github.com/LivingWithHippos/torbox-kmp-client) and uses the same structure and libraries (Ktor, kotlinx.serialization, Kermit).

Supported targets: JVM, Android, iOS, Linux x64, JS (Node).

## Importing the library

**build.gradle.kts**

```kts
dependencies {
    implementation("io.github.livingwithhippos:real-debrid-kmp-client:0.1.0")
}
```

## Usage

```kt
suspend fun main() {
    val client = RealDebridClient.create(RealDebridClientConfig(), RealDebridAuthState(bearerToken = "YOUR-API-TOKEN"))

    // you can also create one without token
    // val client = RealDebridClient.create(RealDebridClientConfig())
    // and update it when you retrieve it
    // client.updateBearerToken("YOUR-API-TOKEN")

    // retrieve the torrent list
    val response = client.torrents.list(limit = 50)
    if (response is ApiResult.Success) {
        println("${response.value.totalCount} torrents")
        response.value.items.forEach { println(it.filename) }
    } else {
        println("Error: $response")
    }
}
```

### Authentication

You can use your [private API token](https://real-debrid.com/apitoken) for personal projects.
Public apps should use OAuth2. The open source app flow is supported out of the box:

```kt
val client = RealDebridClient.create(RealDebridClientConfig())

// 1. get a code and show it to the user with the verification url
val code = (client.oauth.deviceCode() as ApiResult.Success).value
println("Go to ${code.verificationUrl} and insert ${code.userCode}")

// 2. poll until the user authorizes the app
var credentials: CredentialsDto? = null
while (credentials == null) {
    delay(code.interval * 1000L)
    credentials = (client.oauth.credentials(code.deviceCode) as? ApiResult.Success)?.value
}

// 3. get the token and use it
val token = (client.oauth.token(credentials.clientId, credentials.clientSecret, code.deviceCode) as ApiResult.Success).value
client.updateBearerToken(token.accessToken)

// 4. when the token expires (token.expiresIn seconds), refresh it
val newToken = client.oauth.refreshToken(credentials.clientId, credentials.clientSecret, token.refreshToken)
```

Store the client id, client secret and refresh token, not the access token alone.

## Structure

The syntax follows [the official APIs](https://api.real-debrid.com/), the categories are the same, so `Torrents -> GET /torrents/info/{id}` becomes `client.torrents.info(id)`.

| Property | Endpoints |
|---|---|
| `oauth` | device code, device credentials, token, refresh token |
| `user` | user info, disable access token |
| `unrestrict` | link, folder, container file, container link |
| `downloads` | list, delete |
| `torrents` | list, info, available hosts, add torrent, add magnet, select files, delete |
| `hosts` | status, regex, folder regex |
| `streaming` | transcode |

Every call returns an `ApiResult`:

```kt
sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>

    sealed interface Failure : ApiResult<Nothing> {
        // Real-Debrid returned an error body, like {"error": "bad_token", "error_code": 8}
        data class Api(val httpCode: Int, val error: RealDebridErrorDto) : Failure
        // any other http error
        data class Http(val code: Int, val body: String?) : Failure
        // the parameters were not valid, the request was not sent
        data class BadRequest(val cause: Throwable) : Failure
        data class Network(val cause: Throwable) : Failure
        data class Serialization(val cause: Throwable, val body: String?) : Failure
    }
}
```

Real-Debrid responses are not wrapped, so `value` is the requested object directly.
Endpoints that return nothing (204 No Content) return `ApiResult<Unit>`.
Paginated lists return `PagedResponseDto`, with the `items` and the `totalCount` from the `X-Total-Count` header.

Errors can be checked with the documented error codes:

```kt
if (response is ApiResult.Failure.Api && response.error.code == RealDebridErrorCode.BAD_TOKEN) {
    // refresh the token
}
```

## Using it with torbox-kmp-client

The two libraries use different packages, so they can be used in the same project.
A few public names are shared (`ApiResult`, `TorrentDto`, `TorrentFileDto`, `StreamDto`, `UserApi`):
if you need both in the same file, use an import alias:

```kt
import com.github.livingwithhippos.realdebrid.network.ApiResult as RdResult
import com.github.livingwithhippos.realdebrid.model.TorrentDto as RdTorrentDto
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.model.TorrentDto
```

## Notes

Test files using the real API are available under `jvmTest` and `jsTest`, without the `@Test` keyword on calls needing a token.
Copy `example.local.properties` to `local.properties` (or use `mise.local.toml` for the JS tests) and add `@Test` back to run them.

## Donate

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/livingwithhippos)

Subscribe to Real-Debrid [with my referral link](http://real-debrid.com/?id=78841)
