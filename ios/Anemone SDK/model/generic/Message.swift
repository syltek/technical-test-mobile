//
//  Message.swift
//  Anemone SDK
//
//  Created by Manuel González Villegas on 14/3/17.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct Message {
    public let message: String
    public let status: String

    public init(message: String, status: String) {
        self.message = message
        self.status = status
    }
}

extension Message: JSONMappable {
    public init(json: JSONObject) throws {
        self.message = try json.getString("localized_message")
        self.status = try json.getString("status")
    }
}

extension Message: Equatable { }
public func == (lhs: Message, rhs: Message) -> Bool {
    lhs.status == rhs.status && lhs.message == rhs.message
}
