package com.anemonesdk.general.client

import com.anemonesdk.general.exception.AnemoneException
import com.anemonesdk.general.json.JSONTransformer
import com.anemonesdk.model.generic.Message
import com.playtomic.foundation.logger.Log
import com.playtomic.foundation.promise.Promise
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import org.json.JSONException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by manuelgonzalezvillegas on 7/6/17.
 */

class OkHttpClient(
    private val baseUrl: String,
    private val client: OkHttpClient,
    private val urlEncoder: IHttpParameterEncoder = HttpUrlParameterEncoder(),
    private val bodyEncoders: List<IHttpParameterEncoder> = listOf(HttpJsonParameterEncoder(), HttpUrlParameterEncoder(), HttpMultipartParameterEncoder(HttpJsonParameterEncoder()))
) : IHttpClient {
    private val logBuffer = Buffer()

    constructor(
        baseUrl: String,
        timeOut: Long? = null,
        urlEncoder: IHttpParameterEncoder = HttpUrlParameterEncoder(),
        bodyEncoders: List<IHttpParameterEncoder> = listOf(HttpJsonParameterEncoder(), HttpUrlParameterEncoder(), HttpMultipartParameterEncoder(HttpJsonParameterEncoder())),
        interceptors: List<Interceptor> = listOf(),
        networkInterceptors: List<Interceptor> = listOf(),
        eventListenerFactory: okhttp3.EventListener.Factory? = null
    ) : this(
        baseUrl = baseUrl,
        client = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .apply { timeOut?.let { timeout(it) } }
            .apply { interceptors.forEach { addInterceptor(it) } }
            .apply { networkInterceptors.forEach { addNetworkInterceptor(it) } }
            .apply { eventListenerFactory?.let { eventListenerFactory(it) } }
            .build(),
        urlEncoder = urlEncoder,
        bodyEncoders = bodyEncoders
    )

    override fun request(httpRequest: HttpRequest): Promise<HttpResponse> {
        val body: RequestBody?
        var urlString = if (hasScheme(httpRequest.url)) httpRequest.url else baseUrl + httpRequest.url

        try {
            body = buildRequestBody(request = httpRequest)
            buildRequestQuery(request = httpRequest)?.let {
                urlString += "?$it"
            }
        } catch (error: Exception) {
            Log.w("HttpClient", " <--- ❌ Error creating request body for $urlString\n$error")
            return Promise(error)
        }

        val requestBuilder = try {
            Request.Builder()
                .url(urlString)
                .header("Accept-Language", Locale.getDefault().language)
        } catch (ex: Exception) {
            Log.w("HttpClient", " <--- ❌ Error creating request for $urlString")
            return Promise(ex)
        }

        encoderFor(httpRequest)?.let { requestBuilder.header("Content-Type", it.contentType(httpRequest)) }
        httpRequest.headers?.forEach {
            requestBuilder.header(it.key, it.value)
        }
        requestBuilder.method(httpRequest.method.description, body)
        val request = requestBuilder.build()

        logBuffer.clear()
        request.body?.writeTo(logBuffer)

        Log.d("HttpClient") { " --->  🚀 ${request.method} ${request.url}\n\t${logBuffer.readUtf8()}" }
        val client = httpRequest.timeout?.let { client.newBuilder().timeout(it).build() } ?: client
        return Promise { fulfill, reject ->
            try {
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        if (e is SocketTimeoutException) {
                            Log.d("HttpClient") { " <--- ❗️ ${request.method} ${request.url}\n\tERROR - TIMEOUT" }
                            reject(AnemoneException.timeout)
                        } else {
                            Log.d("HttpClient") { " <--- ❗️ ${request.method} ${request.url}\n\t$e" }
                            reject(AnemoneException.network(response = HttpResponse(request = httpRequest, response = null, body = null), error = e, serverMessage = null))
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = response.body?.bytes()
                        val httpResponse = HttpResponse(request = httpRequest, response = response, body = data)
                        if (httpResponse.isSuccessful) {
                            Log.d("HttpClient") { " <--- ✅ ${request.method} ${request.url}\n\t${httpResponse.code}\n\t${data?.let { String(it) }}" }
                            fulfill(httpResponse)
                        } else {
                            var message: Message? = null
                            if (data != null && data.isNotEmpty()) {
                                message = getErrorMessage(data = data)
                            }
                            Log.d("HttpClient") { " <--- ❗️ ${request.method} ${request.url} ${httpResponse.code}\n\t${data?.let { String(it) }}" }
                            reject(AnemoneException.network(response = httpResponse, error = null, serverMessage = message))
                        }
                    }
                })
            } catch (exception: Exception) {
                Log.d("HttpClient") { " <--- ❗️ ${request.method} ${request.url}\n\t$exception" }
                reject(AnemoneException.network(response = HttpResponse(request = httpRequest, response = null, body = null), error = exception, serverMessage = null))
            }
        }
    }

    @Throws(JSONException::class)
    fun buildRequestQuery(request: HttpRequest): String? {
        val params = request.queryParams ?: return null

        return urlEncoder.stringEncode(params)
    }

    @Throws(JSONException::class)
    fun buildRequestBody(request: HttpRequest): RequestBody? {
        if (request.method == HttpMethod.get || request.method == HttpMethod.delete || request.method == HttpMethod.head) {
            return null
        }
        val params = request.bodyParams ?: mapOf<String, Any>()
        val encoder = encoderFor(request) ?: throw JSONException("No body encoder for ${request.contentType}")

        if (params is MultipartBody) {
            val multipartBody = okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM)

            params.multipartBodyParams.forEach {
                multipartBody.addFormDataPart(
                    it.name, null,
                    encoder.dataEncode(it).toRequestBody("application/json".toMediaTypeOrNull(), 0)
                )
            }

            params.multipartDataFiles.forEach { mpDataFile ->
                multipartBody.addFormDataPart(
                    mpDataFile.name, mpDataFile.fileName,
                    mpDataFile.data.toRequestBody(mpDataFile.contentType.toMediaTypeOrNull(), 0)
                )
            }
            return multipartBody.build()
        }
        return encoder.dataEncode(params).toRequestBody(encoder.contentType(request).toMediaTypeOrNull(), 0)
    }

    fun encoderFor(httpRequest: HttpRequest): IHttpParameterEncoder? {
        val type = httpRequest.contentType ?: run { return bodyEncoders.firstOrNull() }
        return bodyEncoders.firstOrNull { type.startsWith(it.contentType(httpRequest)) }
    }

    private fun getErrorMessage(data: ByteArray): Message? =
        JSONTransformer<Message>(Message::class.java).transformObject(data)

    private fun hasScheme(endpoint: String): Boolean =
        endpoint.startsWith("http://") || endpoint.startsWith("https://")
}

fun HttpResponse(request: HttpRequest, response: Response?, body: ByteArray?) =
    HttpResponse(request = request, headers = response?.headers?.toMap() ?: mapOf(), code = response?.code ?: 0, body = body ?: ByteArray(0))

fun Headers.toMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    this.names().forEach { name ->
        val values = values(name)
        map.put(name, values.joinToString(separator = ","))
    }
    return map
}

private fun OkHttpClient.Builder.timeout(timeout: Long) =
    connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .writeTimeout(timeout, TimeUnit.SECONDS)
