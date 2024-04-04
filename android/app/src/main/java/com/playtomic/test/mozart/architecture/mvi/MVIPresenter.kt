package com.playtomic.mozart.architecture.mvi

import com.playtomic.foundation.executor.Executor
import com.playtomic.foundation.observable.MutableObservable
import com.playtomic.foundation.observable.Observable

interface BaseMVIPresenter<ViewState, ViewAction> {
    val currentViewState: ViewState get() = viewState.value!!
    val viewState: Observable<ViewState>
    fun dispatch(action: ViewAction)
}

abstract class MVIPresenter<S : ViewState, A : ViewAction, R : ActionResult>(initialState: S) : BaseMVIPresenter<S, A> {
    override val viewState: Observable<S>
        get() = _viewState
    private val _viewState = MutableObservable(value = initialState)
    internal var middlewares = mutableListOf<MVIMiddleware<S, A, R>>()

    abstract fun handle(action: A, results: (R) -> Unit)

    abstract fun reduce(currentViewState: S, result: R): S

    override fun dispatch(action: A) {
        middlewares.forEach { element ->
            element.handle(action = action, presenter = this)
        }
        handle(action) { result ->
            Executor.execute(inBackground = false) {
                this.middlewares.forEach { middleware ->
                    middleware.handle(result = result, presenter = this)
                }
                this._viewState.value = this.reduce(currentViewState = this.currentViewState, result = result)
            }
        }
    }
}

fun <S : ViewState, A : ViewAction, R : ActionResult, T : MVIPresenter<S, A, R>> T.with(middleware: MVIMiddleware<S, A, R>): T {
    middlewares.add(middleware)
    return this
}
