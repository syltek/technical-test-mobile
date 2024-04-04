package com.anemonesdk.service.request

/**
 * Created by agarcia on 24/03/2017.
 */

data class PaginationOptions internal constructor(
    val page: Int?,
    val size: Int?
) {

    constructor() : this(null, null)

    fun withPage(page: Int?) = copy(page = page)

    fun withSize(size: Int?) = copy(size = size)

    internal val params: Map<String, Any>
        get() {
            val params = mutableMapOf<String, Any>()
            if (page != null) {
                params["page"] = page
            }
            if (size != null) {
                params["size"] = size
            }
            return params
        }
}
