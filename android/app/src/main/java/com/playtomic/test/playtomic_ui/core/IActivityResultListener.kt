package com.playtomicui.core

import android.content.Intent

interface IActivityResultListener {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}
