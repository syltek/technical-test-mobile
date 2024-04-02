//
//  SwiftUIViewController.swift
//  Mozart
//
//  Created by Angel Luis Garcia on 16/11/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//

import Foundation
import SwiftUI

open class SwiftUIViewController<S: ViewState, A: ViewAction, Content: View>: MVIViewController<S, A> {
    private var viewState: ObservableViewState<S>?

    override open func render(state: S) {
        if viewState == nil {
            viewState = ObservableViewState(value: state)
            let hostingViewController = contentView()
                .toHostingController()
            attachChild(viewController: hostingViewController, container: view)
        } else {
            viewState?.value = state
        }
    }

    open func contentView(viewState _: ObservableViewState<S>, dispatcher _: @escaping (A) -> Void) -> Content {
        fatalError("Content view needs to be implemented")
    }

    open func contentView() -> Content {
        loadViewIfNeeded()
        guard let viewState else { fatalError("ViewState not provided in \(self)") }
        return contentView(viewState: viewState) { [weak self] action in
            self?.dispatch(action: action)
        }
    }

    open func anyView() -> AnyView {
        AnyView(contentView())
    }
}
