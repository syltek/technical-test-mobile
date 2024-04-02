//
//  Property.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 9/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public typealias PropertyId = Id

public struct Property {
    public static let durationPropertyId = PropertyId("duration")
    public static let sizePropertyId = PropertyId("resource_size")
    public static let typePropertyId = PropertyId("resource_type")
    public static let featurePropertyId = PropertyId("resource_feature")

    public let id: PropertyId
    public let name: String
    public let options: [PropertyOption]

    public func optionWithId(_ id: PropertyOptionId) -> PropertyOption? {
        options.filter { $0.id == id }.first
    }
}

extension Property: JSONMappable {
    public init(json: JSONObject) throws {
        self.id = try PropertyId(json.getAny("property_id"))
        self.name = try json.getString("name")
        self.options = try json.getJSONArray("options").flatMap { (json: JSONObject) in
            try PropertyOption(json: json)
        }
    }
}

extension Property: Equatable { }
public func == (lhs: Property, rhs: Property) -> Bool {
    lhs.id == rhs.id
}
