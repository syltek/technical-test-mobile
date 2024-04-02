//
//  Coordinate.swift
//  Anemone SDK
//
//  Created by Angel Garcia on 09/12/2016.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import CoreLocation
import Foundation

public struct Coordinate: Equatable, Hashable {
    public let latitude: Double
    public let longitude: Double

    public init(lat: Double, lon: Double) {
        self.latitude = lat
        self.longitude = lon
    }

    public func distance(from coordinate: Coordinate, euclidian: Bool) -> Double {
        if euclidian {
            // mtenes: Used for calculating the plain distance between two points. More context: https://app.clickup.com/t/865cfkm50?comment=90080034677210
            return sqrt(pow(coordinate.longitude - longitude, 2) + pow(coordinate.latitude - latitude, 2)) * 100.0 *
                1000.0 // 1º longitude ≈ 100km * 1000m
        } else {
            let location1 = CLLocation(latitude: latitude, longitude: longitude)
            let location2 = CLLocation(latitude: coordinate.latitude, longitude: coordinate.longitude)

            return location1.distance(from: location2)
        }
    }

    public func formattedDescription() -> String {
        String(format: "(%.2f, %.2f)", latitude, longitude)
    }
}

public extension Coordinate {
    init(_ coordinate: CLLocationCoordinate2D) {
        self.latitude = coordinate.latitude
        self.longitude = coordinate.longitude
    }
}

extension Coordinate: CustomStringConvertible {
    public var description: String {
        "\(latitude),\(longitude)"
    }
}

public extension Coordinate? {
    func coordinateIsEquivalentTo(_ other: Coordinate?, toleranceInMeters: Double) -> Bool {
        if self == other { return true }
        guard let coord1 = self, let coord2 = other else { return false }
        return coord1.distance(from: coord2, euclidian: false) <= toleranceInMeters
    }
}
