//
//  Dictionary+Utils.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 29/06/2018.
//  Copyright © 2018 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public func += (left: inout [String: Any], right: [String: Any]) {
    for (key, value) in right {
        left[key] = value
    }
}

public extension Dictionary {
    var nonEmpty: [Key: Value]? {
        isEmpty ? nil : self
    }

    static func += (left: inout [Key: Value], right: [Key: Value]) {
        for (key, value) in right {
            left.updateValue(value, forKey: key)
        }
    }

    static func + (left: [Key: Value], right: [Key: Value]) -> [Key: Value] {
        var new = left
        for (key, value) in right {
            new.updateValue(value, forKey: key)
        }
        return new
    }

    func mapKeys<R>(transform: (Element) -> R) -> [R: Value] {
        var result = [R: Value]()
        for element in self {
            result[transform(element)] = element.value
        }
        return result
    }
}
