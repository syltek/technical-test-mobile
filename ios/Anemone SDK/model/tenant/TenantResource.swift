//
//  ResourceAvailability.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 9/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//
// swiftlint:disable line_length

import Foundation

public typealias TenantResourceId = Id
public typealias DurationInMinutes = Int

public struct TenantResource {
    public let id: TenantResourceId
    public let name: String
    public let sportId: SportId
    public let properties: [PropertyId: PropertyOptionId]
    public let priority: Int

    // agarcia: Lazy property does not make the trick on sorting because it mutates the original object
    // Dirty hack: Use reference semantics to keep same object and mutate its state on first access
    public var number: Int? { lazyEvaluator.numberIn(name: name) }
    private let lazyEvaluator = TenantResourceLazyEvaluator()
    
    public let allowedDurationIncrements: [DurationInMinutes]
}

public extension TenantResource {
    func fulfillsFilters(_ filters: [PropertyId: [PropertyOptionId]]?) -> Bool {
        guard let filters = filters else { return true }
        
        for (filterId, optionIds) in filters where filterId != Property.durationPropertyId {
            guard
                let resourceOptionId = self.properties[filterId],
                optionIds.contains(resourceOptionId) || filterId == Property.durationPropertyId
            else {
                return false
            }
        }
        return true
    }
}

extension TenantResource: JSONMappable {
    public init(json: JSONObject) throws {
        self.id = try TenantResourceId(json.getAny("resource_id"))
        self.name = try json.getString("name")
        self.sportId = try SportId(json.getAny("sport_id"))
        self.priority = json.optInt("reservation_priority") ?? 0
        guard let propertyValues = try? json.getJSONObject("properties").asMap() else {
            throw AnemoneException.jsonInvalidFormat(key: "properties")
        }
        var propertyMap = [PropertyId: PropertyOptionId]()
        try propertyValues.forEach { entry in
            propertyMap[PropertyId(entry.key)] = try PropertyOptionId(entry.value)
        }
        self.properties = propertyMap
        self.allowedDurationIncrements = (try? json.getJSONObject("booking_settings").getIntArray("allowed_duration_increments")) ?? []
    }
}

extension TenantResource: JSONSerializable {
    public func toJson() -> JSONObject {
        let json = JSONObject()
        json.setString("resource_id", id.description)
        json.setString("name", name)
        json.setString("sport_id", sportId.description)
        json.setInt("reservation_priority", priority)
        let propertiesJson = JSONObject()
        properties.forEach { key, value in propertiesJson.setString(key.description, value.description) }
        json.setObject("properties", propertiesJson)
        let allowedDurationIncrementsObject = JSONObject()
        allowedDurationIncrementsObject.setIntArray("allowed_duration_increments", allowedDurationIncrements)
        json.setObject("booking_settings", allowedDurationIncrementsObject)
        return json
    }
}

// MARK: - Sorting related logic

// swiftlint:disable force_try identifier_name force_unwrapping
private let numberRegex = try! NSRegularExpression(pattern: "\\d+", options: [])

private class TenantResourceLazyEvaluator {
    var _nameNumberEvaluation: Int?
    var _nameNumberEvaluated = false

    func numberIn(name: String) -> Int? {
        guard !_nameNumberEvaluated else { return _nameNumberEvaluation }
        _nameNumberEvaluation = numberRegex.firstMatch(in: name, options: [], range: NSRange(name.startIndex ..< name.endIndex, in: name)).let {
            Int(name[Range($0.range, in: name)!])
        }
        _nameNumberEvaluated = true
        return _nameNumberEvaluation
    }
}

// swiftlint:enable force_try identifier_name force_unwrapping

extension TenantResource: Comparable {
    public static func == (lhs: TenantResource, rhs: TenantResource) -> Bool {
        lhs.id == rhs.id
    }

    public static func < (lhs: TenantResource, rhs: TenantResource) -> Bool {
        if lhs.priority != rhs.priority {
            return lhs.priority < rhs.priority
        }
        let number1 = lhs.number ?? 0
        let number2 = rhs.number ?? 0
        if number1 != number2 {
            return number1 < number2
        }
        return lhs.name < rhs.name
    }
}
