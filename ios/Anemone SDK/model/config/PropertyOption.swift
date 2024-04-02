//
//  PropertyOption.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 9/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public typealias PropertyOptionId = Id

public struct PropertyOption {
    public static let singleSizePropertyOption = PropertyOptionId("single")
    public static let doubleSizePropertyOption = PropertyOptionId("double")
    public static let miniSizePropertyOption = PropertyOptionId("mini")

    public let id: PropertyOptionId
    public let name: String
    public let filterable: Bool

    public init(id: PropertyOptionId, name: String, filterable: Bool = true) {
        self.id = id
        self.name = name
        self.filterable = filterable
    }
}

extension PropertyOption: JSONMappable {
    public init(json: JSONObject) throws {
        self.id = try PropertyOptionId(json.getAny("option_id"))
        self.name = try json.getString("name")
        self.filterable = (try? json.getBoolean("filterable")) ?? true
    }
}

extension PropertyOption: Equatable { }
public func == (lhs: PropertyOption, rhs: PropertyOption) -> Bool {
    lhs.id == rhs.id
}
