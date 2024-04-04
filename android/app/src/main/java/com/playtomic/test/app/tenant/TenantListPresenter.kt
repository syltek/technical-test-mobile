package com.playtomic.test.app.tenant

import com.playtomic.mozart.architecture.mvi.MVIPresenter


class TenantListPresenter(initialState: TenantListViewState) : MVIPresenter<TenantListViewState, TenantListViewAction, TenantListActionResult>(initialState) {

    override fun handle(action: TenantListViewAction, results: (TenantListActionResult) -> Unit) {
        // TODO: Implement
    }

    override fun reduce(currentViewState: TenantListViewState, result: TenantListActionResult): TenantListViewState {
        // TODO: Implement
        return currentViewState
    }
}
