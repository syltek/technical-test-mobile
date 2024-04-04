package com.playtomicui.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

/**
 * Created by agarcia on 23/01/2017.
 */

class PermissionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requestPermission(permissions)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        requestPermission(permissions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val grantedPermissions = HashMap<String, Boolean>()
        for (i in permissions.indices) {
            val permission = permissions[i]
            val value = grantResults[i]
            grantedPermissions.put(permission, value == PackageManager.PERMISSION_GRANTED)
        }
        callback?.invoke(grantedPermissions)
        callback = null
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun requestPermission(permissions: Array<String>?) {
        if (permissions == null || permissions.isEmpty()) {
            finish()
        } else {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    companion object {

        private var callback: ((grantedMap: Map<String, Boolean>) -> Unit)? = null
        private var permissions: Array<String>? = null

        fun requestPermissions(context: Context, permissions: Array<String>, callback: (grantedMap: Map<String, Boolean>) -> Unit) {
            this.callback = callback
            this.permissions = permissions
            val intent = Intent(context, PermissionActivity::class.java)
            if (context is Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }

        fun hasPermission(context: Context, permission: String) =
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

        fun canRequestPermission(context: Activity, permission: String): Boolean =
            !ActivityCompat.shouldShowRequestPermissionRationale(context, permission)
    }
}
