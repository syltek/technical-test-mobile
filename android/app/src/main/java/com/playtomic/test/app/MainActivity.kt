package com.playtomic.test.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anemonesdk.general.client.IHttpClient
import com.anemonesdk.general.client.OkHttpClient
import com.playtomic.foundation.model.IContextProvider
import com.playtomic.test.R
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {
    private val dependencyProvider: DependencyProvider by lazy {
        DependencyProvider(ContextProvider(application, WeakReference(this)))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, dependencyProvider.fragments.rootFragment)
            .commit()
    }

    // Mock implementation for testing purposes
    class ContextProvider(private val application: Application, private val activity: WeakReference<Activity>): IContextProvider {
        override val applicationContext: Context
            get() = application
        override val currentActivity: Activity?
            get() = activity.get()
        override val activityStack: List<Activity>
            get() = activity.get()?.let { listOf(it) } ?: listOf()
    }

}