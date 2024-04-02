//
//  CancelationPolicy.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 19/12/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct CancelationPolicy: Equatable {
    public let duration: Duration?
    public let sportId: SportId
}

extension CancelationPolicy: JSONMappable {
    public init(json: JSONObject) throws {
        if let duration = try? json.getJSONObject("duration") {
            self.duration = try Duration(json: duration)
        } else {
            self.duration = nil
        }
        self.sportId = try SportId(json.getAny("sport_id"))
    }
}
