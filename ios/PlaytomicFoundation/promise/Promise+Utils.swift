//
//  Promise+Utils.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 21/01/2019.
//  Copyright © 2019 Syltek Solutions S.L. All rights reserved.
//
// swiftlint:disable large_tuple function_parameter_count

import Foundation
import PromiseKit

public func whenAll<T>(promises: [Promise<T>]) -> Promise<[T]> {
    if promises.isEmpty {
        return Promise(value: [])
    }
    return Promise { fulfill, reject in
        let internalPromises = promises.map { $0.promise }
        when(fulfilled: internalPromises).done(fulfill).catch(reject)
    }
}

public func whenAll<T1, T2>(_ promise1: Promise<T1>, _ promise2: Promise<T2>) -> Promise<(T1, T2)> {
    Promise { fulfill, reject in
        when(fulfilled: promise1.promise, promise2.promise).done(fulfill).catch(reject)
    }
}

public func whenAll<T1, T2, T3>(_ promise1: Promise<T1>, _ promise2: Promise<T2>, _ promise3: Promise<T3>) -> Promise<(T1, T2, T3)> {
    Promise { fulfill, reject in
        when(fulfilled: promise1.promise, promise2.promise, promise3.promise).done(fulfill).catch(reject)
    }
}

public func whenAll<T1, T2, T3, T4>(
    _ promise1: Promise<T1>,
    _ promise2: Promise<T2>,
    _ promise3: Promise<T3>,
    _ promise4: Promise<T4>
) -> Promise<(T1, T2, T3, T4)> {
    Promise { fulfill, reject in
        when(fulfilled: promise1.promise, promise2.promise, promise3.promise, promise4.promise).done(fulfill).catch(reject)
    }
}

public func whenAll<T1, T2, T3, T4, T5>(
    _ promise1: Promise<T1>,
    _ promise2: Promise<T2>,
    _ promise3: Promise<T3>,
    _ promise4: Promise<T4>,
    _ promise5: Promise<T5>
) -> Promise<(T1, T2, T3, T4, T5)> {
    Promise { fulfill, reject in
        when(fulfilled: promise1.promise, promise2.promise, promise3.promise, promise4.promise, promise5.promise).done(fulfill).catch(reject)
    }
}

public func whenAll<T1, T2, T3, T4, T5, T6>(
    _ promise1: Promise<T1>,
    _ promise2: Promise<T2>,
    _ promise3: Promise<T3>,
    _ promise4: Promise<T4>,
    _ promise5: Promise<T5>,
    _ promise6: Promise<T6>
) -> Promise<(T1, T2, T3, T4, T5, T6)> {
    Promise { fulfill, reject in
        whenAll(
            whenAll(promise1, promise2, promise3),
            whenAll(promise4, promise5, promise6)
        ).then { fulfill(($0.0, $0.1, $0.2, $1.0, $1.1, $1.2)) }
            .catchError { reject($0) }
    }
}

public func whenAll<T1, T2, T3, T4, T5, T6, T7>(
    _ promise1: Promise<T1>,
    _ promise2: Promise<T2>,
    _ promise3: Promise<T3>,
    _ promise4: Promise<T4>,
    _ promise5: Promise<T5>,
    _ promise6: Promise<T6>,
    _ promise7: Promise<T7>
) -> Promise<(T1, T2, T3, T4, T5, T6, T7)> {
    Promise { fulfill, reject in
        whenAll(
            whenAll(promise1, promise2, promise3),
            whenAll(promise4, promise5, promise6, promise7)
        ).then { fulfill(($0.0, $0.1, $0.2, $1.0, $1.1, $1.2, $1.3)) }
            .catchError { reject($0) }
    }
}

public func whenAll<T1, T2, T3, T4, T5, T6, T7, T8>(
    _ promise1: Promise<T1>,
    _ promise2: Promise<T2>,
    _ promise3: Promise<T3>,
    _ promise4: Promise<T4>,
    _ promise5: Promise<T5>,
    _ promise6: Promise<T6>,
    _ promise7: Promise<T7>,
    _ promise8: Promise<T8>
) -> Promise<(T1, T2, T3, T4, T5, T6, T7, T8)> {
    Promise { fulfill, reject in
        whenAll(
            whenAll(promise1, promise2, promise3),
            whenAll(promise4, promise5, promise6),
            whenAll(promise7, promise8)
        ).then { fulfill(($0.0, $0.1, $0.2, $1.0, $1.1, $1.2, $2.0, $2.1)) }
            .catchError { reject($0) }
    }
}

public func whenAll<T1, T2, T3, T4, T5, T6, T7, T8, T9>(
    _ promise1: Promise<T1>,
    _ promise2: Promise<T2>,
    _ promise3: Promise<T3>,
    _ promise4: Promise<T4>,
    _ promise5: Promise<T5>,
    _ promise6: Promise<T6>,
    _ promise7: Promise<T7>,
    _ promise8: Promise<T8>,
    _ promise9: Promise<T9>
) -> Promise<(T1, T2, T3, T4, T5, T6, T7, T8, T9)> {
    Promise { fulfill, reject in
        whenAll(
            whenAll(promise1, promise2, promise3),
            whenAll(promise4, promise5, promise6),
            whenAll(promise7, promise8, promise9)
        ).then { fulfill(($0.0, $0.1, $0.2, $1.0, $1.1, $1.2, $2.0, $2.1, $2.2)) }
            .catchError { reject($0) }
    }
}

public func whenAll<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(
    _ promise1: Promise<T1>,
    _ promise2: Promise<T2>,
    _ promise3: Promise<T3>,
    _ promise4: Promise<T4>,
    _ promise5: Promise<T5>,
    _ promise6: Promise<T6>,
    _ promise7: Promise<T7>,
    _ promise8: Promise<T8>,
    _ promise9: Promise<T9>,
    _ promise10: Promise<T10>
) -> Promise<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)> {
    Promise { fulfill, reject in
        whenAll(
            whenAll(promise1, promise2, promise3),
            whenAll(promise4, promise5, promise6),
            whenAll(promise7, promise8, promise9, promise10)
        )
        .then { fulfill(($0.0, $0.1, $0.2, $1.0, $1.1, $1.2, $2.0, $2.1, $2.2, $2.3)) }
        .catchError { reject($0) }
    }
}

public func whenAll<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(
    _ promise1: Promise<T1>,
    _ promise2: Promise<T2>,
    _ promise3: Promise<T3>,
    _ promise4: Promise<T4>,
    _ promise5: Promise<T5>,
    _ promise6: Promise<T6>,
    _ promise7: Promise<T7>,
    _ promise8: Promise<T8>,
    _ promise9: Promise<T9>,
    _ promise10: Promise<T10>,
    _ promise11: Promise<T11>
) -> Promise<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)> {
    Promise { fulfill, reject in
        whenAll(
            whenAll(promise1, promise2, promise3),
            whenAll(promise4, promise5, promise6, promise7),
            whenAll(promise8, promise9, promise10, promise11)
        )
        .then { fulfill(($0.0, $0.1, $0.2, $1.0, $1.1, $1.2, $1.3, $2.0, $2.1, $2.2, $2.3)) }
        .catchError { reject($0) }
    }
}

public func resolve<T>(
    retries: Int,
    delay: TimeInterval,
    retryCondition: @escaping (Error) -> Bool = { _ in true },
    promiseProvider: @escaping () -> Promise<T>
) -> Promise<T> {
    Promise { fulfill, reject in
        resolve(
            retries: retries,
            delay: delay,
            retryCondition: retryCondition,
            promiseProvider: promiseProvider,
            onFulfill: fulfill,
            onError: reject
        )
    }
}

private func resolve<T>(
    retries: Int,
    delay: TimeInterval,
    retryCondition: @escaping (Error) -> Bool,
    promiseProvider: @escaping () -> Promise<T>,
    onFulfill: @escaping (T) -> Void,
    onError: @escaping (Error) -> Void
) {
    promiseProvider()
        .then(onFulfill)
        .catchError { error in
            if retries > 0 && retryCondition(error) {
                Executor.execute(after: delay, inBackground: false) {
                    resolve(
                        retries: retries - 1,
                        delay: delay,
                        retryCondition: retryCondition,
                        promiseProvider: promiseProvider,
                        onFulfill: onFulfill,
                        onError: onError
                    )
                }
            } else {
                onError(error)
            }
        }
}

public extension Promise {
    /**
     * If the given filter-lambda returns true, this method returns the current Promise,
     * otherwise it returns a failed Promise with the given error
     */
    func filterOrError(error: some Error, filter: @escaping (T) -> Bool) -> Promise<T> {
        then {
            if !filter($0) {
                throw error
            }
        }
    }

    func proceedIfFeatureFlag(featureFlag: Bool) -> Promise<T> {
        filterOrError(error: PlaytomicError.featureFlagDisabled) { _ in featureFlag }
    }

    func mapError(_ map: @escaping (Error) -> some Error) -> Promise<T> {
        fulfillOnError(promise: { Promise(error: map($0)) })
    }
}
