package com.playtomic.mozart.architecture.mvi

abstract class MVIMiddleware<V : ViewState, A : ViewAction, R : ActionResult> {

    open fun handle(action: A, presenter: MVIPresenter<V, A, R>) {}

    open fun handle(result: R, presenter: MVIPresenter<V, A, R>) {}
}
