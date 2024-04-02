//
//  Location.swift
//  My Sports
//
//  Created by Angel Garcia on 12/12/2016.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import CoreLocation
import Foundation

public struct Location: Equatable {
    public let timestamp: Date
    public let accuracy: Double?
    public let coordinate: Coordinate

    public init(timestamp: Date, accuracy: Double?, coordinate: Coordinate) {
        self.timestamp = timestamp
        self.accuracy = accuracy
        self.coordinate = coordinate
    }
}

extension Location {
    init(_ location: CLLocation) {
        self.timestamp = location.timestamp
        self.accuracy = location.horizontalAccuracy
        self.coordinate = Coordinate(location.coordinate)
    }
}
