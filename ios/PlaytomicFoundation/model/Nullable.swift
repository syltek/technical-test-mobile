//
//  Nullable.swift
//  PlaytomicFoundation
//
//  Created by Angel Luis Garcia on 8/4/22.
//  Copyright © 2022 Playtomic. All rights reserved.
//
// This class is a helper for implementing copy methods or alike, where you need to distinguish between
// a real explicit nil passed to the method from a default value passed in an optional parameter
//
// Example usage:
//
// extension Data {
//    func copy(name: Nullable<String> = .none) -> Data {
//        return Data(name: name ?? self.name)
//    }
// }
//
// data.copy(name: .value(nil))

import Foundation

public enum Nullable<T> {
    case none
    case value(T?)

    public func or(_ defValue: T?) -> T? {
        switch self {
        case .none: return defValue
        case let .value(value): return value
        }
    }
}

public func ?? <T>(_ nullable: Nullable<T>, _ defValue: T?) -> T? {
    nullable.or(defValue)
}
