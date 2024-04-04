package com.anemonesdk.general.client

import com.playtomic.foundation.model.CustomStringConvertible

/**
 * Created by manuelgonzalezvillegas on 25/8/17.
 */
enum class HttpMethod : CustomStringConvertible {
    get,
    post,
    put,
    patch,
    delete,
    head;

    override val description get() = name.uppercase()
}
