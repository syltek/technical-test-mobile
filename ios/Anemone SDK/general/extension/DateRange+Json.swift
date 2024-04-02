//
//  DateRange+Json.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 1/2/21.
//  Copyright © 2021 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public extension TimeZoneDateRange {
    static func from(json: JSONObject) throws -> TimeZoneDateRange {
        TimeZoneDateRange(
            from: try json.getDate("from"),
            to: try json.getDate("to"),
            timeZone: TimeZone(identifier: json.optString("timezone") ?? "") ?? TimeZone.current
        )
    }

    func toJson() -> JSONObject {
        let json = JSONObject()
        json.setDate("from", from)
        json.setDate("to", to)
        json.setString("timezone", timeZone.identifier)
        return json
    }
}
