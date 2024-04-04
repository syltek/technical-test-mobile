package com.playtomic.mozart.architecture.mvi

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class ComposeFragment<S : ViewState, A : ViewAction> : MVIFragment<S, A>() {
    var composeView: ComposeView? = null
        private set
    private val viewStateLiveData = MutableLiveData<S>()

    @Composable
    abstract fun ContentView(viewState: LiveData<S>, dispatcher: ((A) -> Unit))

    @Composable
    fun ContentView() = ContentView(viewStateLiveData, ::dispatch)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return ComposeView(requireContext()).apply {
            // Dispose the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ContentView()
            }
        }
    }

    override fun render(state: S) {
        viewStateLiveData.postValue(state)
    }
}

