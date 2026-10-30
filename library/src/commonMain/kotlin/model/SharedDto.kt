package com.github.livingwithhippos.realdebrid.model

/**
 * A page of results from a paginated endpoint.
 *
 * @property items the items in this page
 * @property totalCount the total number of items, read from the X-Total-Count header.
 *  Null if the header was missing.
 */
data class PagedResponseDto<T>(
    val items: List<T>,
    val totalCount: Int?,
)
