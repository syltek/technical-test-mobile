package com.playtomic.test.app.tenant

import com.playtomic.mozart.architecture.mvi.NoActionResult
import com.playtomic.mozart.architecture.mvi.NoViewAction
import com.playtomic.mozart.architecture.mvi.ViewState


data class TenantListViewState(
    // TODO: Implement
    val text: String
) : ViewState

// TODO: Implement
typealias TenantListViewAction = NoViewAction
typealias TenantListActionResult = NoActionResult
