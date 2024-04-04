package com.playtomicui.core

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment

open class PlaytomicFragment : Fragment() {

    @CallSuper
    override fun onResume() {
        super.onResume()

        pendingFragments.forEach {
            childFragmentManager
                .beginTransaction()
                .add(it.second, it.first)
                .commitNowAllowingStateLoss()
        }
        pendingFragments.clear()
    }

    val pendingFragments = mutableListOf<Pair<Fragment, Int>>()

    fun isChild(view: Fragment): Boolean =
        pendingFragments.any { it.first == view } || (host != null && childFragmentManager.fragments.contains(view))

    fun attachChild(fragment: Fragment, container: ViewGroup, tag: String? = null) {
        if (isChild(view = fragment)) {
            return
        }
        if (container.id == View.NO_ID) {
            container.id = View.generateViewId()
        }

        try {
            // Replace repeated child fragments by tag (ex: coming from view restoration) or class if no tag provided
            val existingFragment = if (tag != null) childFragmentManager.findFragmentByTag(tag) else
                childFragmentManager.fragments.firstOrNull { it.javaClass == fragment.javaClass }
            if (existingFragment != null) {
                childFragmentManager
                    .beginTransaction()
                    .replace(container.id, fragment, tag)
                    .commitNowAllowingStateLoss()
            }
            // Or add it as a new fragment
            else {
                childFragmentManager
                    .beginTransaction()
                    .add(container.id, fragment, tag)
                    .commitNowAllowingStateLoss()
            }
        } catch (ex: IllegalStateException) {
            pendingFragments.add(fragment to container.id)
            if (isResumed) {
                Handler(Looper.getMainLooper()).post {
                    val pair = pendingFragments.firstOrNull { it.first == fragment } ?: return@post
                    pendingFragments.remove(pair)
                    attachChild(fragment, container, tag)
                }
            }
        }
    }

    fun detachChild(fragment: Fragment) {
        pendingFragments.firstOrNull { it.first == fragment }?.let {
            pendingFragments.remove(it)
        }
        if (!isChild(view = fragment)) {
            return
        }
        childFragmentManager
            .beginTransaction()
            .remove(fragment)
            .commitNowAllowingStateLoss()
    }
}
