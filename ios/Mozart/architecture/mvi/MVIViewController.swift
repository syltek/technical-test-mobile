//
//  MVIViewController.swift
//  Mozart
//
//  Created by Angel Luis Garcia on 21/9/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//

import Foundation

open class MVIViewController<S: ViewState, A: ViewAction>: PlaytomicViewController {
    typealias Presenter = BaseMVIPresenter<S, A>
    private let disposal = Disposal()
    var presenter: Presenter?
    var childId: String?

    override open func viewDidLoad() {
        super.viewDidLoad()
        presenter?.viewState.observe { [weak self] state in
            if let state {
                self?.render(state: state)
            }
        }.add(to: disposal)
    }

    open func render(state _: S) {
        fatalError("Must be implemented by the children")
    }

    public func dispatch(action: A) {
        presenter?.dispatch(action: action)
    }
}
