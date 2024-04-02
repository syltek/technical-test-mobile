//
//  SportWarning.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 27/12/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct SportWarning {
    public let propertyId: PropertyId
    public let optionId: PropertyOptionId
    public let message: String
}

extension SportWarning: JSONMappable {
    public init(json: JSONObject) throws {
        self.propertyId = try PropertyId(json.getAny("property_id"))
        self.optionId = try PropertyOptionId(json.getAny("option_id"))
        self.message = try json.getString("message")
    }
}
