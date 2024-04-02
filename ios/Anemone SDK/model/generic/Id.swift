//
//  Id.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 11/08/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct Id: CustomStringConvertible, Hashable, Encodable {
    public let value: String

    public init(_ value: Any) throws {
        if let value = value as? Int {
            self.init(value)
        } else if let value = value as? String {
            self.init(value)
        } else if let value = value as? Substring {
            self.init(value)
        } else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
    }

    public init(_ value: Int) {
        self.value = "\(value)"
    }

    public init(_ value: String) {
        self.value = value
    }

    public init(_ value: Substring) {
        self.value = String(value)
    }

    public var description: String {
        value
    }
}
