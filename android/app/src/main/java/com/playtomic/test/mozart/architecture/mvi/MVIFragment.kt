package com.playtomic.mozart.architecture.mvi

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.playtomicui.core.PlaytomicFragment

abstract class MVIFragment<S : ViewState, A : ViewAction> : PlaytomicFragment {
    internal var presenter: BaseMVIPresenter<S, A>? = null
    private var attachedLifecycleOwner: LifecycleOwner? = null

    private var isManagedAsChild = false

    constructor() : super()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachTo(viewLifecycleOwner)
    }

    abstract fun render(state: S)

    fun dispatch(action: A) {
        presenter?.dispatch(action = action)
    }

    fun attachTo(lifeCycleOwner: LifecycleOwner) {
        if (attachedLifecycleOwner == lifeCycleOwner) { return }
        attachedLifecycleOwner?.let { presenter?.viewState?.removeObservers(it) }
        presenter?.viewState?.observe(lifeCycleOwner, ::render)
        attachedLifecycleOwner = lifeCycleOwner
    }
}
