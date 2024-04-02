//
//  Observable.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 19/03/2019.
//  Copyright © 2019 Syltek Solutions S.L. All rights reserved.
//

import Foundation

open class Observable<T> {
    public typealias Observer = (T?) -> Void

    private var observers: [Int: (Observer, DispatchQueue?)] = [:] {
        didSet {
            hasObservers = !observers.isEmpty
        }
    }
    open var hasObservers: Bool = false
    private var uniqueID = (0...).makeIterator()

    fileprivate var _value: T? {
        didSet {
            synchronized(self) {
                let value = _value
                observers.values.forEach { observer, dispatchQueue in
                    if let dispatchQueue = dispatchQueue {
                        dispatchQueue.async {
                            observer(value)
                        }
                    } else {
                        observer(value)
                    }
                }
            }
        }
    }

    open var value: T? {
        _value
    }

    fileprivate init() { }

    open func observe(_ queue: DispatchQueue? = DispatchQueue.main, _ observer: @escaping Observer) -> Disposable {
        synchronized(self) {
            // swiftlint:disable force_unwrapping
            let id = uniqueID.next()!
            // swiftlint:enable force_unwrapping

            observers[id] = (observer, queue)

            let disposable = Disposable { [weak self] in
                self?.observers[id] = nil
            }

            if let value = self.value {
                observer(value)
            }
            return disposable
        }
    }

    open func removeAllObservers() {
        synchronized(self) {
            observers.removeAll()
        }
    }
}

public extension Observable {
    func singleValue() -> Promise<T> {
        Promise<T> { fulfill, _ in
            if let value = self.value {
                fulfill(value)
            } else {
                let disposal = Disposal()
                self.observe { value in
                    if let value = value {
                        disposal.dispose()
                        fulfill(value)
                    }
                }.add(to: disposal)
            }
        }
    }

    func whenValue(condition: @escaping (T) -> Bool) -> Promise<T> {
        Promise<T> { fulfill, _ in
            if let value = self.value, condition(value) {
                fulfill(value)
            } else {
                let disposal = Disposal()
                self.observe { value in
                    if let value = value, condition(value) {
                        disposal.dispose()
                        fulfill(value)
                    }
                }.add(to: disposal)
            }
        }
    }

    func observeNext(_ queue: DispatchQueue? = DispatchQueue.main, _ observer: @escaping Observer) -> Disposable {
        if value == nil {
            return observe(queue, observer)
        } else {
            // Ignore first event since it is a previous value
            var isFirstEvent = true
            return observe(queue) { value in
                if isFirstEvent {
                    isFirstEvent = false
                } else {
                    observer(value)
                }
            }
        }
    }

    func observeChange(_ queue: DispatchQueue? = DispatchQueue.main, observer: @escaping ((T?, T?) -> Void)) -> Disposable {
        var previousValue = value
        return observeNext(queue) { newValue in
            observer(previousValue, newValue)
            previousValue = newValue
        }
    }

    func map<R>(_ map: @escaping (T) -> R) -> Observable<R> {
        MappedObservable(original: self, transform: map)
    }
}

open class MutableObservable<T>: Observable<T> {
    override public init() { }
    public init(value: T?) {
        super.init()
        _value = value
    }

    override open var value: T? {
        get {
            _value
        }
        set {
            _value = newValue
        }
    }
}

public extension MutableObservable where T: Equatable {
    var distinctValue: T? {
        get {
            _value
        }
        set {
            if value != newValue {
                value = newValue
            }
        }
    }
}

class MappedObservable<T, R>: Observable<R> {
    let original: Observable<T>
    let transform: (T) -> R
    init(original: Observable<T>, transform: @escaping (T) -> R) {
        self.original = original
        self.transform = transform
    }

    override var hasObservers: Bool {
        get { original.hasObservers }
        set { original.hasObservers = newValue }
    }

    override var value: R? {
        original.value.map(transform)
    }

    override func observe(_ queue: DispatchQueue? = DispatchQueue.main, _ observer: @escaping Observable<R>.Observer) -> Disposable {
        original.observe { [transform] in
            if let value = $0 {
                observer(transform(value))
            }
        }
    }

    override func removeAllObservers() {
        original.removeAllObservers()
    }

}
