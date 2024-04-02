//
//  Optional+Utils.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 18/05/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public extension Optional {
    @inline(__always)
    func `let`(_ closure: (Wrapped) -> Void) {
        if let val = self {
            closure(val)
        }
    }

    @discardableResult
    @inline(__always)
    func `let`<T>(_ closure: (Wrapped) -> T?) -> T? {
        if let val = self {
            return closure(val)
        } else {
            return nil
        }
    }

    @discardableResult
    @inline(__always)
    func also(_ closure: (Wrapped) -> Void) -> Wrapped? {
        if let val = self {
            closure(val)
        }
        return self
    }

    /**
     If self is nil, it returns the given parameter otherwise it returns self.
     'value: A' is evaluated anyway, even if discarded, so better use .or(takeValue: () -> A) when working with Promises.
     */
    func or(_ value: Wrapped) -> Wrapped {
        self ?? value
    }

    /**
     If self is nil, it returns the computation of the given lambda otherwise it returns self.
     This implementation is Promise friendly.
     */
    func or(takeValue: () -> Wrapped) -> Wrapped {
        self ?? takeValue()
    }

    /**
     If self is nil, it returns the given parameter otherwise it returns self
     */
    func orNullable(_ value: Wrapped?) -> Wrapped? {
        self ?? value
    }

    /**
     * If filter expression is true, then return current value, if not return null.
     */
    @inline(__always)
    func takeIf(_ expression: (Wrapped) -> Bool) -> Wrapped? {
        self.let { expression($0) ? self : nil }
    }

    /**
     If self is nil, it returns the given parameter otherwise it returns self
     */
    func or(throw error: Error) throws -> Wrapped {
        if let self {
            return self
        } else {
            throw error
        }
    }

    /**
     if self is nil, the lambda param is executed, otherwise not
      */
    @discardableResult
    @inline(__always)
    func ifNilExecute(_ expression: () -> Void) -> Wrapped? {
        if self == nil {
            expression()
        }
        return self
    }

    /**
     * Transform a nullable A into a Promise<A>.
     * If A is null, the resulted Promise will be a failed one with specified error.
     */
    func toPromise(_ error: Error) -> Promise<Wrapped> {
        self.let { Promise(value: $0) }.or(takeValue: { Promise(error: error) })
    }
}

public extension Dictionary {
    @discardableResult
    @inline(__always)
    func `let`<T>(_ closure: (Dictionary) -> T) -> T {
        closure(self)
    }
}

/**
 If all the given values are not nil, it returns a tuple within all parameters, otherwise it returns nil
 */
public func allNotNull<A, B>(_ value1: A?, _ value2: B?) -> (A, B)? {
    guard let value1, let value2 else { return nil }

    return (value1, value2)
}

/**
 If all the given values are not nil, it returns a tuple within all parameters, otherwise it returns nil
 */
public func allNotNull<A, B, C>(_ value1: A?, _ value2: B?, _ value3: C?) -> (A, B, C)? {
    guard let value1, let value2, let value3 else { return nil }

    return (value1, value2, value3)
}

/**
 If all the given values are not nil, it returns a tuple within all parameters, otherwise it returns nil
 */
public func allNotNull<A, B, C, D>(_ value1: A?, _ value2: B?, _ value3: C?, _ value4: D?) -> (A, B, C, D)? {
    guard let value1, let value2, let value3, let value4 else { return nil }

    return (value1, value2, value3, value4)
}
