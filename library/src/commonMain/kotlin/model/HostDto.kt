package com.github.livingwithhippos.realdebrid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Status of a supported host. The hosts status endpoint returns a map using the host domain as key.
 *
 * @property supported 0 or 1
 * @property status "up", "down" or "unsupported"
 * @property checkTime jsonDate
 * @property competitorsStatus map of competitor domain to its status for this host
 */
@Serializable
data class HostStatusDto(
    val id: String,
    val name: String,
    val image: String,
    @SerialName("image_big") val imageBig: String? = null,
    val supported: Int,
    val status: String,
    @SerialName("check_time") val checkTime: String,
    @SerialName("competitors_status") val competitorsStatus: Map<String, CompetitorStatusDto> = emptyMap(),
)

/**
 * @property status "up", "down" or "unsupported"
 * @property checkTime jsonDate
 */
@Serializable
data class CompetitorStatusDto(
    val status: String,
    @SerialName("check_time") val checkTime: String,
)
