//
//  Sport.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 9/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct SportId: CustomStringConvertible, Hashable {
    public let value: String

    public init(_ value: Any) throws {
        if let value = value as? Int {
            self.init(value)
        } else if let value = value as? String {
            self.init(value)
        } else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
    }

    public init(_ value: Int) {
        self.init("\(value)")
    }

    public init(_ value: String) {
        self.value = value == "1" ? "PADEL"
            : value == "2" ? "TENNIS"
            : value
    }

    public var description: String {
        value
    }

    // swiftlint:disable identifier_name
    public static let PADEL = SportId("PADEL")
    public static let TENNIS = SportId("TENNIS")
    public static let PADBOL = SportId("PADBOL")
    public static let SQUASH = SportId("SQUASH")
    public static let BADMINTON = SportId("BADMINTON")
    public static let FOOTBALL7 = SportId("FOOTBALL7")
    public static let FOOTBALL11 = SportId("FOOTBALL11")
    public static let FUTSAL = SportId("FUTSAL")
    public static let FOOTBALL_OTHERS = SportId("FOOTBALL_OTHERS")
    public static let PICKLEBALL = SportId("PICKLEBALL")
    public static let BEACH_TENNIS = SportId("BEACH_TENNIS")
    public static let BEACH_VOLLEY = SportId("BEACH_VOLLEY")
    public static let BASKETBALL = SportId("BASKETBALL")
    public static let VOLLEYBALL = SportId("VOLLEYBALL")
    public static let TABLE_TENNIS = SportId("TABLE_TENNIS")
    public static let CRICKET = SportId("CRICKET")
    // swiftlint:enable identifier_name
}

public struct Sport {
    public let id: SportId
    public let name: String
    public let isRacket: Bool
    public let properties: [Property]
    public let warnings: [SportWarning]
}

extension Sport: JSONMappable {
    public init(json: JSONObject) throws {
        self.id = try SportId(json.getAny("sport_id"))
        self.name = try json.getString("name")
        self.isRacket = try json.getBoolean("is_racket")
        self.properties = try json.getJSONArray("properties").flatMap { (json: JSONObject) in
            try? Property(json: json)
        }
        self.warnings = (try? json.getJSONArray("warnings").flatMap { (json: JSONObject) in
            try? SportWarning(json: json)
        }) ?? []
    }
}

public extension Sport {
    func propertyWithId(_ id: PropertyId) -> Property? {
        properties.filter { $0.id == id }.first
    }

    func warningFor(propertyId: PropertyId, optionId: PropertyOptionId) -> SportWarning? {
        warnings.filter { $0.propertyId == propertyId && $0.optionId == optionId }.first
    }

    func filterProperties(_ filterIds: [PropertyId]) -> [Property] {
        properties.filter {
            filterIds.map { $0.value }.contains($0.id.value)
        }
    }
}

extension Sport: Equatable { }
public func == (lhs: Sport, rhs: Sport) -> Bool {
    lhs.id == rhs.id
}
