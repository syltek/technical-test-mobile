//
//  Tenant.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 9/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//
// swiftlint:disable identifier_name line_length colon

import Foundation

public typealias TenantId = Id
 
public struct Tenant {
    public let id: TenantId
    public let name: String
    public let description: String?
    public let address: Address
    public let images: [String]
    public let resources: [TenantResource]
    public let sportIds: [SportId]
    public let type: TenantType?
    public let bookingType: BookingType
    public let cancelationPolicies: [CancelationPolicy]?
    public let openingHours: [String: TimeRange]?
}

public extension Tenant {
    func fulfillsFilters(_ filters: [PropertyId: [PropertyOptionId]]?) -> Bool {
        guard let filters = filters else { return true }
        for resource in resources {
            if resource.fulfillsFilters(filters) {
                return true
            }
        }
        return false
    }

    func resources(sportId: SportId) -> [TenantResource] {
        resources.filter { $0.sportId == sportId }
    }
}

extension Tenant: JSONMappable {
    public init(json: JSONObject) throws {
        id = try TenantId(json.getAny("tenant_id"))
        name = try json.optString("tenant_name") ?? json.getString("name")
        address = try Address(json: json.getJSONObject("address"))
        images = try json.getStringArray("images")
        description = try? json.getString("description")
        sportIds = try json.getAnyArray("sport_ids").map { try SportId($0) }
        type = (try? json.getString("tenant_type")).let { TenantType.fromRawValue($0) }
        resources = json.optJSONArray("resources")?.flatMap { obj in try? TenantResource(json: obj) } ?? []
        let bookingTypeStr = try json.getString("booking_type")
        guard let bookingType = BookingType.fromRawValue(bookingTypeStr) else {
            throw AnemoneException.jsonInvalidFormat(key: "booking_type")
        }
        self.bookingType = bookingType
        cancelationPolicies = try? json.getJSONArray("cancelation_policies").flatMap { try CancelationPolicy(json: $0) }
        if let openingJson = json.optJSONObject("opening_hours") {
            var hours = [String: TimeRange]()
            openingJson.keys().forEach { day in
                if
                    let from = try? Time(openingJson.getJSONObject(day).getString("opening_time")),
                    let to = try? Time(openingJson.getJSONObject(day).getString("closing_time"))
                {
                    hours[day] = TimeRange(from: from, to: to)
                }
            }
            self.openingHours = hours
        } else {
            self.openingHours = nil
        }
    }
}

extension Tenant: JSONSerializable {
    public func toJson() -> JSONObject {
        let json = JSONObject()
        json.setString("tenant_id", id.value)
        json.setString("tenant_name", name)
        json.setObject("address", address.toJson())
        json.setArray("images", images)
        json.setString("description", description)
        json.setArray("sport_ids", sportIds.map { $0.value })
        json.setString("booking_type", bookingType.description)
        return json
    }
}

extension Tenant: Equatable { }
public func == (lhs: Tenant, rhs: Tenant) -> Bool {
    lhs.id == rhs.id
}

extension Tenant: Hashable {
    public func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}
