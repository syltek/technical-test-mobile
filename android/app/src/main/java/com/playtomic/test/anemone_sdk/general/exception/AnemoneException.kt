package com.anemonesdk.general.exception

import com.anemonesdk.general.client.HttpResponse
import com.anemonesdk.model.generic.Message

/**
 * Created by agarcia on 02/02/2017.
 */

sealed class AnemoneException : Exception {

    constructor()

    constructor(message: String?) : super(message)

    constructor(cause: Exception) : super(cause)

    object notFound : AnemoneException()
    data class jsonInvalidFormat(val key: String?) : AnemoneException(key?.let { "Wrong key: $it" })
    object notMappable : AnemoneException()
    object timeout : AnemoneException()
    object denied : AnemoneException()
    object wrongUrl : AnemoneException()
    data class network(val response: HttpResponse, val error: Exception?, val serverMessage: Message?) : AnemoneException()
    data class configurationMissing(val error: String) : AnemoneException(error)
    data class generic(val error: String?) : AnemoneException(error)

    override fun getLocalizedMessage(): String =
        when (this) {
            AnemoneException.notFound -> "Anemone not found exception"
            is AnemoneException.jsonInvalidFormat -> "Anemone json invalid exception. $message"
            AnemoneException.notMappable -> "Anemone not mappable exception"
            AnemoneException.timeout -> "Anemone timeout exception"
            AnemoneException.denied -> "Anemone denied exception"
            AnemoneException.wrongUrl -> "Anemone wrong url exception"
            is AnemoneException.network -> serverMessage?.message ?: error?.localizedMessage ?: "Anemone network exception"
            is configurationMissing -> error
            is generic -> error ?: "Anemone generic exception"
            else -> "Anemone exception"
        }

    val isConnectivityError: Boolean
        get() = when (this) {
            is timeout -> true
            is network -> response.code <= 0
            else -> false
        }

    val isPrivateTenantError: Boolean
        get() = when (this) {
            is network -> serverMessage?.status == "BOOKING_PRIVATE"
            else -> false
        }

    val isForbiddenError: Boolean
        get() = when (this) {
            is network -> response.code == 403
            else -> false
        }

    val isNotFoundError: Boolean
        get() = when (this) {
            is network -> response.code == 404
            is notFound -> true
            else -> false
        }
}
