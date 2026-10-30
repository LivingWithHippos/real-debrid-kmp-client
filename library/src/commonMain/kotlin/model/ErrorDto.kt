package com.github.livingwithhippos.realdebrid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Error body returned by Real-Debrid together with a 4XX/5XX http code.
 *
 * [online documentation](https://api.real-debrid.com/#api_error_codes)
 */
@Serializable
data class RealDebridErrorDto(
    val error: String,
    @SerialName("error_details") val errorDetails: String? = null,
    @SerialName("error_code") val errorCode: Int? = null,
) {
    /** The known error matching [errorCode], or null if missing or unknown */
    val code: RealDebridErrorCode?
        get() = errorCode?.let { RealDebridErrorCode.fromCode(it) }
}

/**
 * Error codes documented by Real-Debrid.
 *
 * [online documentation](https://api.real-debrid.com/#api_error_codes)
 */
enum class RealDebridErrorCode(
    val code: Int,
) {
    INTERNAL_ERROR(-1),
    MISSING_PARAMETER(1),
    BAD_PARAMETER_VALUE(2),
    UNKNOWN_METHOD(3),
    METHOD_NOT_ALLOWED(4),
    SLOW_DOWN(5),
    RESOURCE_UNREACHABLE(6),
    RESOURCE_NOT_FOUND(7),
    BAD_TOKEN(8),
    PERMISSION_DENIED(9),
    TWO_FACTOR_AUTH_NEEDED(10),
    TWO_FACTOR_AUTH_PENDING(11),
    INVALID_LOGIN(12),
    INVALID_PASSWORD(13),
    ACCOUNT_LOCKED(14),
    ACCOUNT_NOT_ACTIVATED(15),
    UNSUPPORTED_HOSTER(16),
    HOSTER_IN_MAINTENANCE(17),
    HOSTER_LIMIT_REACHED(18),
    HOSTER_TEMPORARILY_UNAVAILABLE(19),
    HOSTER_NOT_AVAILABLE_FOR_FREE_USERS(20),
    TOO_MANY_ACTIVE_DOWNLOADS(21),
    IP_ADDRESS_NOT_ALLOWED(22),
    TRAFFIC_EXHAUSTED(23),
    FILE_UNAVAILABLE(24),
    SERVICE_UNAVAILABLE(25),
    UPLOAD_TOO_BIG(26),
    UPLOAD_ERROR(27),
    FILE_NOT_ALLOWED(28),
    TORRENT_TOO_BIG(29),
    TORRENT_FILE_INVALID(30),
    ACTION_ALREADY_DONE(31),
    IMAGE_RESOLUTION_ERROR(32),
    TORRENT_ALREADY_ACTIVE(33),
    TOO_MANY_REQUESTS(34),
    INFRINGING_FILE(35),
    FAIR_USAGE_LIMIT(36),
    DISABLED_ENDPOINT(37),
    ;

    companion object {
        fun fromCode(code: Int): RealDebridErrorCode? = entries.firstOrNull { it.code == code }
    }
}
