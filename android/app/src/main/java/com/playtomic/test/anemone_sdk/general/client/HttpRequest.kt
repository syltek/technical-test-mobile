package com.anemonesdk.general.client

/**
 * Created by manuelgonzalezvillegas on 25/8/17.
 */

data class HttpRequest private constructor(
    val method: HttpMethod,
    val url: String,
    val queryParams: Map<String, Any>?,
    val bodyParams: Any?,
    val headers: Map<String, String>?,
    val timeout: Long?
) {

    val contentType get() = headers?.get("Content-Type")

    companion object {
        val includeTotalHeaderKey: String = "X-Playtomic-Include-Total-Header"
        val includeTotalHeaderValue: String = "true"

        fun get(url: String, params: Map<String, Any>?, timeout: Long? = null): HttpRequest =
            HttpRequest(method = HttpMethod.get, url = url, queryParams = params, bodyParams = null, headers = null, timeout = timeout)

        fun post(url: String, params: Any?, timeout: Long? = null): HttpRequest =
            HttpRequest(method = HttpMethod.post, url = url, queryParams = null, bodyParams = params, headers = null, timeout = timeout)

        fun post(url: String, params: Any?, headers: Map<String, String>?): HttpRequest =
            HttpRequest(method = HttpMethod.post, url = url, queryParams = null, bodyParams = params, headers = headers, timeout = null)

        fun post(url: String, multipartBodyParams: List<MultipartBodyParam>, multipartDataFiles: List<MultipartDataFile>, timeout: Long? = null): HttpRequest =
            HttpRequest(
                method = HttpMethod.post,
                url = url,
                queryParams = null,
                bodyParams = MultipartBody(multipartBodyParams, multipartDataFiles),
                headers = mapOf(Pair("Content-Type", "multipart/form-data")),
                timeout = timeout
            )

        fun put(url: String, params: Any?, timeout: Long? = null): HttpRequest =
            HttpRequest(method = HttpMethod.put, url = url, queryParams = null, bodyParams = params, headers = null, timeout = timeout)

        fun put(url: String, multipartBodyParams: List<MultipartBodyParam>, multipartDataFiles: List<MultipartDataFile>, timeout: Long? = null): HttpRequest =
            HttpRequest(
                method = HttpMethod.put,
                url = url,
                queryParams = null,
                bodyParams = MultipartBody(multipartBodyParams, multipartDataFiles),
                headers = mapOf(Pair("Content-Type", "multipart/form-data")),
                timeout = timeout
            )

        fun patch(url: String, params: Any?, timeout: Long? = null): HttpRequest =
            HttpRequest(method = HttpMethod.patch, url = url, queryParams = null, bodyParams = params, headers = null, timeout = timeout)

        fun patch(url: String, multipartBodyParams: List<MultipartBodyParam>, multipartDataFiles: List<MultipartDataFile>, timeout: Long? = null): HttpRequest =
            HttpRequest(
                method = HttpMethod.patch,
                url = url,
                queryParams = null,
                bodyParams = MultipartBody(multipartBodyParams, multipartDataFiles),
                headers = mapOf(Pair("Content-Type", "multipart/form-data")),
                timeout = timeout
            )

        fun delete(url: String, params: Map<String, Any>?, timeout: Long? = null): HttpRequest =
            HttpRequest(method = HttpMethod.delete, url = url, queryParams = params, bodyParams = null, headers = null, timeout = timeout)

        fun head(url: String, params: Map<String, Any>?, headers: Map<String, String>? = null, timeout: Long? = null): HttpRequest =
            HttpRequest(method = HttpMethod.head, url = url, queryParams = params, bodyParams = null, headers = headers , timeout = timeout)
    }
}
