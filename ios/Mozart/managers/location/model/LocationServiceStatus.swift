//
//  LocationServiceStatus.swift
//  My Sports
//
//  Created by Angel Garcia on 16/12/2016.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import CoreLocation
import Foundation

public enum LocationServiceStatus {
    case notDetermined
    case denied
    case authorized
    case disabled

    init(status: CLAuthorizationStatus) {
        switch status {
        case .denied, .restricted:
            self = .denied
        case .notDetermined:
            self = .notDetermined
        case .authorizedAlways, .authorizedWhenInUse:
            self = .authorized
        @unknown default:
            self = .notDetermined
        }
    }
}
