package com.github.livingwithhippos.realdebrid.model

import kotlinx.serialization.Serializable

/**
 * @property points fidelity points
 * @property type "premium" or "free"
 * @property premium seconds left as a Premium user
 * @property expiration jsonDate
 */
@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val points: Int,
    val locale: String,
    val avatar: String,
    val type: String,
    val premium: Long,
    val expiration: String?,
)
