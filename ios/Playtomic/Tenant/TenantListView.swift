//
//  TenantListView.swift
//  Playtomic
//
//  Created by Angel Luis Garcia on 2/4/24.
//

import SwiftUI

class TenantListViewController: SwiftUIViewController<TenantListViewState, TenantListViewAction, TenantListView> {

    override func contentView(
        viewState: ObservableViewState<TenantListViewState>,
        dispatcher: @escaping (TenantListViewAction) -> Void
    ) -> TenantListView {
        TenantListView(viewState: viewState, dispatcher: dispatcher)
    }
}

struct TenantListView: View {
    @ObservedObject var viewState: ObservableViewState<TenantListViewState>
    let dispatcher: (TenantListViewAction) -> Void

    var body: some View {
        // TODO: Implement
        Text(viewState.value.text)
    }
}

#Preview {
    TenantListView(
        viewState: ObservableViewState(value: TenantListViewState(text: "Hello Preview")),
        dispatcher: { _ in }
    )
}
