package com.github.livingwithhippos.realdebrid

import com.github.livingwithhippos.realdebrid.impl.DownloadsApiImpl
import com.github.livingwithhippos.realdebrid.impl.HostsApiImpl
import com.github.livingwithhippos.realdebrid.impl.OAuthApiImpl
import com.github.livingwithhippos.realdebrid.impl.TorrentsApiImpl
import com.github.livingwithhippos.realdebrid.impl.UserApiImpl
import com.github.livingwithhippos.realdebrid.model.RealDebridErrorCode
import com.github.livingwithhippos.realdebrid.network.ApiResult
import com.github.livingwithhippos.realdebrid.network.RealDebridAuthState
import com.github.livingwithhippos.realdebrid.network.RealDebridClientConfig
import com.github.livingwithhippos.realdebrid.network.configureRealDebrid
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MockApiTest {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val requests = mutableListOf<HttpRequestData>()
    private val authState = RealDebridAuthState("TOKEN")

    private fun mockClient(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient =
        HttpClient(
            MockEngine { request ->
                requests.add(request)
                handler(request)
            },
        ) {
            configureRealDebrid(RealDebridClientConfig(), authState)
        }

    private fun json(body: String): suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
        { respond(body, HttpStatusCode.OK, jsonHeaders) }

    private val dispatcher = Dispatchers.Default

    @Test
    fun userIsDecodedAndBearerIsSent() =
        runTest {
            val client =
                mockClient(
                    json(
                        """{"id":1,"username":"hippo","email":"a@b.c","points":10,"locale":"en",
                        "avatar":"https://x/y.png","type":"premium","premium":3600,
                        "expiration":"2032-06-06T04:42:42.000Z","unknown_field":true}""",
                    ),
                )
            val result = UserApiImpl(client, dispatcher).get()

            assertIs<ApiResult.Success<*>>(result)
            assertEquals("hippo", (result as ApiResult.Success).value.username)
            val request = requests.single()
            assertEquals("https://api.real-debrid.com/rest/1.0/user", request.url.toString())
            assertEquals("Bearer TOKEN", request.headers[HttpHeaders.Authorization])
        }

    @Test
    fun updatedTokenIsUsed() =
        runTest {
            val client = mockClient(json("[]"))
            authState.bearerToken = "NEW"
            HostsApiImpl(client, dispatcher).status()
            assertEquals("Bearer NEW", requests.single().headers[HttpHeaders.Authorization])
        }

    @Test
    fun noAuthRequestsSkipTheBearer() =
        runTest {
            val client = mockClient(json("""["regex1", "regex2"]"""))
            val result = HostsApiImpl(client, dispatcher).regex()

            assertEquals(listOf("regex1", "regex2"), (result as ApiResult.Success).value)
            assertNull(requests.single().headers[HttpHeaders.Authorization])
        }

    @Test
    fun oauthUsesItsBaseUrlWithoutBearer() =
        runTest {
            val client =
                mockClient(
                    json(
                        """{"device_code":"DC","user_code":"UC","interval":5,"expires_in":1800,
                        "verification_url":"https://real-debrid.com/device"}""",
                    ),
                )
            val result = OAuthApiImpl(client, dispatcher).deviceCode()

            assertEquals("DC", (result as ApiResult.Success).value.deviceCode)
            val request = requests.single()
            assertEquals("https://api.real-debrid.com/oauth/v2/device/code", request.url.toString().substringBefore("?"))
            assertEquals(OPEN_SOURCE_CLIENT_ID, request.url.parameters["client_id"])
            assertEquals("yes", request.url.parameters["new_credentials"])
            assertNull(request.headers[HttpHeaders.Authorization])
        }

    @Test
    fun oauthTokenIsAForm() =
        runTest {
            val client =
                mockClient(
                    json("""{"access_token":"AT","expires_in":3600,"token_type":"Bearer","refresh_token":"RT"}"""),
                )
            val result = OAuthApiImpl(client, dispatcher).refreshToken("ID", "SECRET", "RT")

            assertEquals("AT", (result as ApiResult.Success).value.accessToken)
            val form = (requests.single().body as FormDataContent).formData
            assertEquals("RT", form["code"])
            assertEquals(DEVICE_GRANT_TYPE, form["grant_type"])
            assertNull(requests.single().headers[HttpHeaders.Authorization])
        }

    @Test
    fun pagedListReadsTotalCount() =
        runTest {
            val client =
                mockClient {
                    respond(
                        """[{"id":"T1","filename":"file","hash":"abc","bytes":10,"host":"real-debrid.com",
                        "split":2000,"progress":100,"status":"downloaded","added":"2032-06-06T04:42:42.000Z",
                        "links":["https://real-debrid.com/d/X"]}]""",
                        HttpStatusCode.OK,
                        headersOf(
                            HttpHeaders.ContentType to listOf("application/json"),
                            "X-Total-Count" to listOf("42"),
                        ),
                    )
                }
            val result = TorrentsApiImpl(client, dispatcher).list(limit = 1, filter = "active")

            val page = (result as ApiResult.Success).value
            assertEquals(42, page.totalCount)
            assertEquals("T1", page.items.single().id)
            assertEquals("active", requests.single().url.parameters["filter"])
        }

    @Test
    fun emptyListIsReturnedOnNoContent() =
        runTest {
            val client = mockClient { respond("", HttpStatusCode.NoContent) }
            val result = DownloadsApiImpl(client, dispatcher).list()

            val page = (result as ApiResult.Success).value
            assertTrue(page.items.isEmpty())
            assertNull(page.totalCount)
        }

    @Test
    fun deleteReturnsUnitOnNoContent() =
        runTest {
            val client = mockClient { respond("", HttpStatusCode.NoContent) }
            val result = TorrentsApiImpl(client, dispatcher).delete("ABC")

            assertEquals(ApiResult.Success(Unit), result)
            assertEquals(HttpMethod.Delete, requests.single().method)
            assertEquals("/rest/1.0/torrents/delete/ABC", requests.single().url.encodedPath)
        }

    @Test
    fun errorBodyIsParsed() =
        runTest {
            val client =
                mockClient {
                    respond("""{"error":"bad_token","error_code":8}""", HttpStatusCode.Unauthorized, jsonHeaders)
                }
            val result = UserApiImpl(client, dispatcher).get()

            assertIs<ApiResult.Failure.Api>(result)
            assertEquals(401, result.httpCode)
            assertEquals(RealDebridErrorCode.BAD_TOKEN, result.error.code)
        }

    @Test
    fun nonJsonErrorIsHttpFailure() =
        runTest {
            val client = mockClient { respond("Service Unavailable", HttpStatusCode.ServiceUnavailable) }
            val result = UserApiImpl(client, dispatcher).get()

            assertIs<ApiResult.Failure.Http>(result)
            assertEquals(503, result.code)
        }

    @Test
    fun brokenJsonIsSerializationFailure() =
        runTest {
            val client = mockClient(json("""{"id": "not a number"}"""))
            val result = UserApiImpl(client, dispatcher).get()

            assertIs<ApiResult.Failure.Serialization>(result)
        }

    @Test
    fun addMagnetSendsAForm() =
        runTest {
            val client = mockClient(json("""{"id":"ID","uri":"https://api.real-debrid.com/rest/1.0/torrents/info/ID"}"""))
            val result = TorrentsApiImpl(client, dispatcher).addMagnet("magnet:?xt=urn:btih:123")

            assertEquals("ID", (result as ApiResult.Success).value.id)
            val request = requests.single()
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("magnet:?xt=urn:btih:123", (request.body as FormDataContent).formData["magnet"])
            assertNull((request.body as FormDataContent).formData["host"])
        }

    @Test
    fun selectFilesJoinsIds() =
        runTest {
            val client = mockClient { respond("", HttpStatusCode.NoContent) }
            val api = TorrentsApiImpl(client, dispatcher)
            api.selectFiles("ID", listOf(1, 3))
            api.selectFiles("ID")

            assertEquals("1,3", (requests[0].body as FormDataContent).formData["files"])
            assertEquals("all", (requests[1].body as FormDataContent).formData["files"])
        }

    @Test
    fun invalidParametersAreNotSent() =
        runTest {
            val client = mockClient(json("{}"))
            val api = TorrentsApiImpl(client, dispatcher)

            assertIs<ApiResult.Failure.BadRequest>(api.addMagnet(" "))
            assertIs<ApiResult.Failure.BadRequest>(api.selectFiles("ID", emptyList()))
            assertIs<ApiResult.Failure.BadRequest>(api.addTorrent(ByteArray(0)))
            assertTrue(requests.isEmpty())
        }

    @Test
    fun hostStatusMapIsDecoded() =
        runTest {
            val client =
                mockClient(
                    json(
                        """{"1fichier.com":{"id":"1f","name":"1Fichier","image":"https://x/1f.png",
                        "supported":1,"status":"up","check_time":"2032-06-06T04:42:42.000Z",
                        "competitors_status":{"other.com":{"status":"down","check_time":"2032-06-06T04:42:42.000Z"}}}}""",
                    ),
                )
            val result = HostsApiImpl(client, dispatcher).status()

            val host = (result as ApiResult.Success).value.getValue("1fichier.com")
            assertEquals(1, host.supported)
            assertEquals("down", host.competitorsStatus.getValue("other.com").status)
        }
}
