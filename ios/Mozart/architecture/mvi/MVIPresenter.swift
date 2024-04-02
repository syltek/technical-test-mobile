//
//  MVIPresenter.swift
//  Mozart
//
//  Created by Angel Luis Garcia on 21/9/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//

import Foundation

open class BaseMVIPresenter<S: ViewState, A: ViewAction> {
    // swiftlint:disable:next force_unwrapping
    public var currentViewState: S { _viewState.value! }
    public var viewState: Observable<S> { _viewState }
    fileprivate let _viewState: MutableObservable<S>

    public init(initialState: S) {
        self._viewState = MutableObservable(value: initialState)
    }

    func dispatch(action _: A) {
        fatalError("Must be implemented by the children")
    }
}

open class MVIPresenter<S: ViewState, A: ViewAction, R: ActionResult>: BaseMVIPresenter<S, A> {
    private var middlewares: [MVIMiddleware<S, A, R>] = []

    public func with(middleware: MVIMiddleware<S, A, R>) -> Self {
        middlewares.append(middleware)
        return self
    }

    open func handle(action _: A, results _: @escaping (R) -> Void) {
        fatalError("Must be implemented by the children")
    }

    open func reduce(currentViewState _: S, result _: R) -> S {
        fatalError("Must be implemented by the children")
    }

    override public func dispatch(action: A) {
        middlewares.forEach { element in
            element.handle(action: action, presenter: self)
        }
        handle(action: action) { [weak self] result in
            Executor.execute(inBackground: false) {
                guard let self else { return }
                self.middlewares.forEach { middleware in
                    middleware.handle(result: result, presenter: self)
                }
                self._viewState.value = self.reduce(currentViewState: self.currentViewState, result: result)
            }
        }
    }
}

