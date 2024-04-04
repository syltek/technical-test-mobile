package com.anemonesdk.general.client

import android.content.Context
import com.anemonesdk.general.exception.AnemoneException
import com.playtomic.foundation.promise.Promise

/**
 * Created by mgonzalez on 21/12/16.
 */

class LocalHttpClient(private val context: Context) : IHttpClient {

    override fun request(httpRequest: HttpRequest): Promise<HttpResponse> {
        var endpoint = httpRequest.url
        if (endpoint.startsWith("/")) {
            endpoint = endpoint.substring(1)
        }
        val path = endpoint + ".json"

        return Promise(executeInBackground = true) { fulfill, reject ->
            try {
                val inputStream = context.assets.open(path)
                val data = inputStream.readBytes()
                inputStream.close()
                fulfill(HttpResponse(request = httpRequest, headers = mapOf(), code = 200, body = data))
            } catch (e: Exception) {
                reject(
                    AnemoneException.network(
                        response = HttpResponse(request = httpRequest, headers = mapOf(), code = 404, body = ByteArray(0)),
                        error = e,
                        serverMessage = null
                    )
                )
            }
        }
    }
}
