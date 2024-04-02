//
//  LocationManager.swift
//  My Sports
//
//  Created by Angel Garcia on 12/12/2016.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import CoreLocation
import Foundation

public final class LocationManager: NSObject, ILocationManager {
    public struct Defaults {
        public static let minAccuracy = 500.0
        public static let maxAge = 180.0
        public static let timeout = 15.0
    }

    private let locationManager: CLLocationManager
    private let locale: Locale

    internal var requests: [LocationRequest] = [] {
        didSet {
            if requests.isEmpty {
                locationManager.stopUpdatingLocation()
            } else {
                updateLocationAccuracy()
                locationManager.startUpdatingLocation()
            }
        }
    }

    public init(locationManager: CLLocationManager = CLLocationManager(), locale: Locale = Locale.current) {
        self.locationManager = locationManager
        self.locale = locale
        super.init()
        self.locationManager.delegate = self
    }

    public var locationStatus: LocationServiceStatus {
        guard type(of: locationManager).locationServicesEnabled() else {
            return .disabled
        }
        let status = locationManager.authorizationStatus
        return LocationServiceStatus(status: status)
    }

    public var lastLocation: Location? {
        guard let location = locationManager.location else { return nil }
        return Location(location)
    }

    public lazy var defaultCoordinate: Coordinate = {
        let countryCode = locale.regionCode?.uppercased() ?? "ES"
        // File origin: http://techslides.com/demos/country-capitals.json
        guard
            let path = Bundle(for: LocationManager.self).path(forResource: "country-capitals", ofType: "json"),
            let data = try? Data(contentsOf: URL(fileURLWithPath: path)),
            let jsonArray = try? JSONSerialization.jsonObject(with: data, options: []) as? [[String: Any]],
            let jsonCountry = jsonArray.first(where: { ($0["CountryCode"] as? String)?.uppercased() == countryCode }),
            let lat = (jsonCountry["CapitalLatitude"] as? String).let({ Double($0) }),
            let lon = (jsonCountry["CapitalLongitude"] as? String).let({ Double($0) })
        else {
            assertionFailure("Country capitals for \(countryCode) not found or not properly parsed")
            // Madrid
            return Coordinate(lat: 40.4168361, lon: -3.7039706)
        }

        return Coordinate(lat: lat, lon: lon)
    }()

    public func findLocation(
        permissionRequest: LocationPermissionRequest,
        minAccuracy: Double,
        maxAge: TimeInterval,
        timeout: TimeInterval
    ) -> Promise<Location> {
        Promise { fullfill, reject in
            let request = LocationRequest(minAccuracy: minAccuracy, maxAge: maxAge, timeout: timeout, fulfill: fullfill, reject: reject)
            self.addRequest(request, permissionRequest: permissionRequest)
        }
    }

    public func hasPermission() -> Bool {
        locationStatus == .authorized
    }

    public func requestLocationPermission() {
        locationManager.requestWhenInUseAuthorization()
    }
}

extension LocationManager: CLLocationManagerDelegate {
    public func locationManager(_: CLLocationManager, didUpdateLocations _: [CLLocation]) {
        guard let location = lastLocation else { return }
        let validRequests = requests.filter { $0.isLocationValid(location: location) }

        validRequests.forEach { request in
            if let index = self.requests.firstIndex(of: request) {
                self.requests.remove(at: index)
            }
            request.timer?.invalidate()
            request.fulfill(location)
        }
    }

    public func locationManager(_: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedWhenInUse || status == .authorizedAlways {
            if !requests.isEmpty {
                locationManager.startUpdatingLocation()
            }
        } else {
            rejectPendingRequests(error: FoundationException.denied)
        }
    }
}

// MARK: Internal utility methods

extension LocationManager {
    func addRequest(_ request: LocationRequest, permissionRequest: LocationPermissionRequest) {
        // If location is rejected, then return error
        if locationStatus == .denied || locationStatus == .disabled {
            request.reject(FoundationException.denied)
            return
        }

        // If last location is already valid then early return
        if
            let lastLocation,
            request.isLocationValid(location: lastLocation)
        {
            request.fulfill(lastLocation)
            return
        }

        // If no permission given yet, then just request it
        if locationStatus == .notDetermined {
            if permissionRequest == .never {
                request.reject(FoundationException.denied)
                return
            }

            requestLocationPermission()
        }

        // If timeout, set timer to be able to fail request after timeout
        if request.timeout > 0 {
            request.timer = Timer.scheduledTimer(
                timeInterval: TimeInterval(request.timeout),
                target: self,
                selector: #selector(LocationManager.requestTimeOut(_:)),
                userInfo: request,
                repeats: false
            )
        }

        requests.append(request)
    }

    func updateLocationAccuracy() {
        let bestAccuracy = requests.reduce(kCLLocationAccuracyThreeKilometers) { acc, req in
            min(req.minAccuracy ?? acc, acc)
        }
        locationManager.desiredAccuracy = bestAccuracy
    }

    @objc
    func requestTimeOut(_ timer: Timer) {
        guard let request = timer.userInfo as? LocationRequest, let index = requests.firstIndex(of: request) else { return }
        request.reject(FoundationException.timeout)
        requests.remove(at: index)
    }

    func rejectPendingRequests(error: Error) {
        requests.forEach { $0.reject(error) }
        requests.removeAll()
    }
}
