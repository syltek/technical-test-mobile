//
//  MVIMiddleware.swift
//  Mozart
//
//  Created by Angel Luis Garcia on 21/9/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//

import Foundation

open class MVIMiddleware<V: ViewState, A: ViewAction, R: ActionResult> {
    public init() { }

    open func handle(action _: A, presenter _: MVIPresenter<V, A, R>) { }
    open func handle(result _: R, presenter _: MVIPresenter<V, A, R>) { }
}
