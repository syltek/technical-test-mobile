//
//  Promise.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 27/01/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation
import PromiseKit

public class Promise<T> {
    var promise: PromiseKit.Promise<T>

    public required init(
        after: TimeInterval = 0,
        executeInBackground: Bool = false,
        resolvers: @escaping (_ fulfill: @escaping (T) -> Void, _ reject: @escaping (Error) -> Void) throws -> Void
    ) {
        self.promise = PromiseKit.Promise { resolver in
            Executor.execute(after: after, inBackground: executeInBackground) {
                do {
                    try resolvers(resolver.fulfill, resolver.reject)
                } catch {
                    resolver.reject(error)
                }
            }
        }
    }

    public required init(value: T) {
        self.promise = PromiseKit.Promise.value(value)
    }

    public required init(error: Error) {
        self.promise = PromiseKit.Promise(error: error)
    }

    internal init(promise: PromiseKit.Promise<T>) {
        self.promise = promise
    }

    public var value: T? {
        promise.value
    }

    public var error: Error? {
        promise.error
    }

    public var isResolved: Bool {
        value != nil || error != nil
    }

    @discardableResult
    public func then(inBackground: Bool = false, _ body: @escaping (T) throws -> Void) -> Promise<T> {
        then(inBackground: inBackground, map: { value in
            try body(value)
            return value
        })
    }

    @discardableResult
    public func then<U>(inBackground: Bool = false, map body: @escaping (T) throws -> U) -> Promise<U> {
        let queue = inBackground ? DispatchQueue.global(qos: .background) : DispatchQueue.main
        return Promise<U>(promise: promise.map(on: queue, body))
    }

    @discardableResult
    public func then<U>(inBackground: Bool = false, promise body: @escaping (T) throws -> Promise<U>) -> Promise<U> {
        let (pendingPromise, resolver) = PromiseKit.Promise<U>.pending()
        promise
            .done { (result: T) in
                Executor.execute(inBackground: inBackground) {
                    do {
                        let deferredPromise = try body(result)
                        deferredPromise
                            .then(map: resolver.fulfill)
                            .catchError(resolver.reject)
                    } catch {
                        resolver.reject(error)
                    }
                }
            }.catch(resolver.reject)

        return Promise<U>(promise: pendingPromise)
    }

    @discardableResult
    public func catchError(_ body: @escaping (Error) -> Void) -> Promise<T> {
        promise.catch { error in
            body(error)
        }
        return Promise<T>(promise: promise)
    }

    @discardableResult
    public func fulfillOnError(value: T) -> Promise<T> {
        Promise<T> { fulfill, _ in
            self.then(fulfill)
                .catchError { _ in fulfill(value) }
        }
    }

    @discardableResult
    public func fulfillOnError(promise errorHandler: @escaping (Error) -> Promise<T>) -> Promise<T> {
        Promise<T> { fulfill, reject in
            self.then(fulfill)
                .catchError { error in
                    errorHandler(error).then(fulfill).catchError(reject)
                }
        }
    }

    @discardableResult
    public func fulfillOnError(map errorHandler: @escaping (Error) -> T?) -> Promise<T> {
        Promise<T> { fulfill, reject in
            self.then(fulfill)
                .catchError { error in
                    if let value = errorHandler(error) {
                        fulfill(value)
                    } else {
                        reject(error)
                    }
                }
        }
    }

    @discardableResult
    public func always(inBackground: Bool = false, _ body: @escaping () -> Void) -> Promise<T> {
        let queue = inBackground ? DispatchQueue.global(qos: .background) : DispatchQueue.main
        return Promise<T>(promise: promise.ensure(on: queue, body))
    }

    @discardableResult
    public func rejectOnTimeout(timeout: Double) -> Promise<T> {
        let (pendingPromise, resolver) = PromiseKit.Promise<T>.pending()
        let task = Executor.execute(after: timeout, inBackground: false) {
            if pendingPromise.isPending {
                resolver.reject(FoundationException.timeout)
            }
        }
        promise
            .done { (result: T) in
                if pendingPromise.isPending {
                    task?.cancel()
                    resolver.fulfill(result)
                }
            }
            .catch {
                if pendingPromise.isPending {
                    task?.cancel()
                    resolver.reject($0)
                }
            }

        return Promise(promise: pendingPromise)
    }

    @discardableResult
    public func ignoreResult() -> Promise<Void> {
        then(map: { _ in })
    }
}
