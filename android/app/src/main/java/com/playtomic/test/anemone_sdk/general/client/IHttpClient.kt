package com.anemonesdk.general.client

import com.playtomic.foundation.promise.Promise

/**
 * Created by mgonzalez on 20/12/16.
 */

interface IHttpClient {

    fun request(httpRequest: HttpRequest): Promise<HttpResponse>

    fun get(endpoint: String, params: Map<String, Any>?): Promise<ByteArray> =
        request(HttpRequest.get(url = endpoint, params = params))
            .then(map = { it.body })

    fun post(endpoint: String, params: Any?): Promise<ByteArray> =
        request(HttpRequest.post(url = endpoint, params = params))
            .then(map = { it.body })

    fun post(endpoint: String, multipartBodyParams: List<MultipartBodyParam>, multipartDataFiles: List<MultipartDataFile>): Promise<ByteArray> =
        request(HttpRequest.post(url = endpoint, multipartBodyParams = multipartBodyParams, multipartDataFiles = multipartDataFiles))
            .then(map = { it.body })

    fun put(endpoint: String, params: Any?): Promise<ByteArray> =
        request(HttpRequest.put(url = endpoint, params = params))
            .then(map = { it.body })

    fun put(endpoint: String, multipartBodyParams: List<MultipartBodyParam>, multipartDataFiles: List<MultipartDataFile>): Promise<ByteArray> =
        request(HttpRequest.put(url = endpoint, multipartBodyParams = multipartBodyParams, multipartDataFiles = multipartDataFiles))
            .then(map = { it.body })

    fun patch(endpoint: String, params: Any?): Promise<ByteArray> =
        request(HttpRequest.patch(url = endpoint, params = params))
            .then(map = { it.body })

    fun patch(endpoint: String, multipartBodyParams: List<MultipartBodyParam>, multipartDataFiles: List<MultipartDataFile>): Promise<ByteArray> =
        request(HttpRequest.patch(url = endpoint, multipartBodyParams = multipartBodyParams, multipartDataFiles = multipartDataFiles))
            .then(map = { it.body })

    fun delete(endpoint: String, params: Map<String, Any>?): Promise<ByteArray> =
        request(HttpRequest.delete(url = endpoint, params = params))
            .then(map = { it.body })
}
