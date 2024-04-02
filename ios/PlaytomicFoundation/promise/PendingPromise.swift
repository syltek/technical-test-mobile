//
//  PendingPromise.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 13/06/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation
import PromiseKit

public class PendingPromise<T>: Promise<T> {
    let pending: (fulfill: (T) -> Void, reject: (Error) -> Void)

    public required init() {
        let (promise, resolver) = PromiseKit.Promise<T>.pending()
        self.pending = (resolver.fulfill, resolver.reject)
        super.init(promise: promise)
    }

    @available(*, unavailable, renamed: "init()")
    public required init(
        after _: TimeInterval = 0,
        executeInBackground _: Bool = false,
        resolvers _: @escaping (_ fulfill: @escaping (T) -> Void, _ reject: @escaping (Error) -> Void) throws -> Void
    ) {
        fatalError()
    }

    @available(*, unavailable, renamed: "init()")
    public required init(error _: Error) {
        fatalError()
    }

    @available(*, unavailable, renamed: "init()")
    public required init(value _: T) {
        fatalError()
    }

    public func fulfill(_ value: T) {
        pending.fulfill(value)
    }

    public func reject(_ error: Error) {
        pending.reject(error)
    }
}
