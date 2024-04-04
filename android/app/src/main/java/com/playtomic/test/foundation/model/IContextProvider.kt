package com.playtomic.foundation.model

import android.app.Activity
import android.content.Context

/**
 * Created by agarcia on 20/01/2017.
 */

interface IContextProvider {

    val applicationContext: Context

    // agarcia: This can be null if last activity killed (pressing back) but app not removed by system yet
    val currentActivity: Activity?

    val activityStack: List<Activity>
}
