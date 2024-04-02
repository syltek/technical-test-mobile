//
//  Coordinate+Json.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 16/06/2020.
//  Copyright © 2020 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public extension Coordinate {
    static func from(json: JSONObject) throws -> Coordinate {
        Coordinate(
            lat: try json.getDouble("lat"),
            lon: try json.getDouble("lon")
        )
    }

    func toJson() -> JSONObject {
        let json = JSONObject()
        json.setDouble("lat", latitude)
        json.setDouble("lon", longitude)

        return json
    }
}
