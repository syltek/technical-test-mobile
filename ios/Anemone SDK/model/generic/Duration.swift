//
//  Duration.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 19/12/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct Duration: Equatable, Hashable {
    public enum Unit: String {
        case seconds = "SECONDS", minutes = "MINUTES", hours = "HOURS", days = "DAYS", weeks = "WEEKS", months = "MONTHS", years = "YEARS"
    }

    public let amount: Int
    public let unit: Duration.Unit

    public init(amount: Int, unit: Duration.Unit) {
        self.amount = amount
        self.unit = unit
    }
}

extension Duration: JSONMappable {
    public init(json: JSONObject) throws {
        self.amount = try json.getInt("amount")
        guard let duration = Duration.Unit.fromRawValue(try json.getString("unit")) else {
            throw AnemoneException.jsonInvalidFormat(key: "unit")
        }
        self.unit = duration
    }
}

extension Duration: JSONSerializable {
    public func toJson() -> JSONObject {
        let json = JSONObject()
        json.setInt("amount", amount)
        json.setString("unit", unit.rawValue)
        return json
    }
}

public extension Duration {
    func toUnit(_ unit: Duration.Unit) -> Duration {
        let selfMultiplier = self.unit.secondsMultiplier
        let unitMultiplier = unit.secondsMultiplier

        return Duration(amount: Int(Double(amount) * selfMultiplier / unitMultiplier), unit: unit)
    }

    func toAccurateUnit() -> Duration {
        var duration = self
        let selfMultiplier = unit.secondsMultiplier

        let seconds = Double(amount) * selfMultiplier / Duration.Unit.seconds.secondsMultiplier
        if seconds == Double(Int(seconds)), seconds >= 60 {
            duration = Duration(amount: Int(seconds), unit: Duration.Unit.seconds)
        }

        let minutes = Double(amount) * selfMultiplier / Duration.Unit.minutes.secondsMultiplier
        if minutes == Double(Int(minutes)), minutes > 1 {
            duration = Duration(amount: Int(minutes), unit: Duration.Unit.minutes)
        }

        let hours = Double(amount) * selfMultiplier / Duration.Unit.hours.secondsMultiplier
        if hours == Double(Int(hours)), hours >= 2 {
            duration = Duration(amount: Int(hours), unit: Duration.Unit.hours)
        }

        let days = Double(amount) * selfMultiplier / Duration.Unit.days.secondsMultiplier
        if days == Double(Int(days)), days > 2 {
            duration = Duration(amount: Int(days), unit: Duration.Unit.days)
        }

        return duration
    }
}

extension Duration.Unit {
    public var secondsMultiplier: Double {
        switch self {
        case .seconds:
            return 1.0
        case .minutes:
            return 60.0
        case .hours:
            return 3600.0
        case .days:
            return 3600.0 * 24.0
        case .weeks:
            return 3600.0 * 24.0 * 7.0
        case .months:
            return 3600.0 * 24.0 * 31.0
        case .years:
            return 3600.0 * 24.0 * 365.0
        }
    }

    static func fromRawValue(_ rawValue: String) -> Duration.Unit? {
        Duration.Unit(rawValue: rawValue.uppercased())
    }
}
