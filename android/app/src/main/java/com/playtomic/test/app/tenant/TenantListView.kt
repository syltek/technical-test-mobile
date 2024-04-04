package com.playtomic.test.app.tenant

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.playtomic.mozart.architecture.mvi.ComposeFragment


class TenantListFragment : ComposeFragment<TenantListViewState, TenantListViewAction>() {
    @Composable
    override fun ContentView(
        viewState: LiveData<TenantListViewState>,
        dispatcher: (TenantListViewAction) -> Unit
    ) {
        TenantListView(viewState, dispatcher)
    }
}

@Composable
fun TenantListView(
    viewState: LiveData<TenantListViewState>,
    dispatcher: (TenantListViewAction) -> Unit
) {
    Text(
        text = viewState.observeAsState().value?.text ?: "",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize().wrapContentHeight(align = Alignment.CenterVertically)
    )
}


@Preview
@Composable
fun TenantListViewPreview() {
    TenantListView(
        viewState = MutableLiveData(TenantListViewState(text = "Hello Preview")),
        dispatcher = { }
    )
}