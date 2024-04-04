package com.anemonesdk.general.client

import com.playtomic.foundation.extension.tryOrNull

/**
 * Created by agarcia on 25/09/2017.
 */
data class HttpResponse(
    val request: HttpRequest,
    val headers: Map<String, String>,
    val code: Int,
    val body: ByteArray
) {

    val isSuccessful: Boolean = code in 200 until 400

    /**
     * Remember that you need to send HttpRequest.includeTotalHeader(Key/Value)
     * in the headers request to receive the total count.
     * You have a few examples:
     * @see com.anemonesdk.service.MatchService.countMatches
     * @see com.anemonesdk.service.AcademyClassService.countCourses
     * @see com.anemonesdk.service.ActivityService.countTournaments
     */
    val totalCount: Int?
        get() = headers["total"]?.let { tryOrNull { it.toInt() } }
}
