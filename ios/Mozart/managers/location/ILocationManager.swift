//
//  ILocationManager.swift
//  My Sports
//
//  Created by Angel Garcia on 12/12/2016.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public protocol ILocationManager {
    var defaultCoordinate: Coordinate { get }
    var lastLocation: Location? { get }
    var locationStatus: LocationServiceStatus { get }

    func findLocation(permissionRequest: LocationPermissionRequest, minAccuracy: Double, maxAge: TimeInterval, timeout: TimeInterval)
        -> Promise<Location>
    func hasPermission() -> Bool
    func requestLocationPermission()
}

public extension ILocationManager {
    var bestKnownCoordinate: Coordinate {
        lastLocation?.coordinate ?? defaultCoordinate
    }

    func findLocation(
        permissionRequest: LocationPermissionRequest,
        minAccuracy: Double = LocationManager.Defaults.minAccuracy,
        maxAge: TimeInterval = LocationManager.Defaults.maxAge,
        timeout: TimeInterval = LocationManager.Defaults.timeout
    ) -> Promise<Location> {
        findLocation(permissionRequest: permissionRequest, minAccuracy: minAccuracy, maxAge: maxAge, timeout: timeout)
    }

    func findOptimisticLocation(
        permissionRequest: LocationPermissionRequest,
        maxAge: TimeInterval = LocationManager.Defaults.maxAge,
        timeout: Double
    ) -> Promise<Location> {
        findLocation(permissionRequest: permissionRequest, maxAge: maxAge, timeout: timeout)
            .fulfillOnError(promise: { error in
                self.lastLocation.map { Promise(value: $0) } ?? Promise(error: error)
            })
    }
}

public enum LocationPermissionRequest {
    case never, always, `default`
}
