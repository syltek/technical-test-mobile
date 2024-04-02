//
//  JSONObject.swift
//  Anemone SDK
//
//  Created by Angel Garcia on 17/01/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public class JSONObject {
    var data: [String: Any]

    public init() {
        self.data = [:]
    }

    init(data: [String: Any]) {
        self.data = data
    }

    public convenience init(string: String) throws {
        guard let data = string.data(using: .utf8) else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
        try self.init(data: data)
    }

    public convenience init(data: Data) throws {
        guard let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
        self.init(data: json)
    }

    public func has(_ key: String) -> Bool {
        data[key] != nil && !(data[key] is NSNull)
    }

    public func keys() -> [String] {
        data.keys.map { $0 }
    }

    public func getAny(_ key: String) throws -> Any {
        guard let value = data[key], !(value is NSNull) else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }

        return value
    }

    public func optAny(_ key: String) -> Any? {
        try? getAny(key)
    }

    public func getInt(_ key: String) throws -> Int {
        guard let value = data[key] as? Int else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return value
    }

    public func optInt(_ key: String) -> Int? {
        try? getInt(key)
    }

    public func getDouble(_ key: String) throws -> Double {
        guard let value = data[key] as? Double else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return value
    }

    public func optDouble(_ key: String) -> Double? {
        try? getDouble(key)
    }

    public func getDecimal(_ key: String, decimals: Int) throws -> Decimal {
        if let value = data[key] as? Double {
            return Decimal(value).roundedValue(decimals: decimals)
        } else if let valueInt = data[key] as? Int {
            return Decimal(Double(valueInt)).roundedValue(decimals: decimals)
        }
        throw AnemoneException.jsonInvalidFormat(key: key)
    }

    public func optDecimal(_ key: String, decimals: Int) -> Decimal? {
        try? getDecimal(key, decimals: decimals)
    }

    public func getBoolean(_ key: String) throws -> Bool {
        guard let value = data[key] as? Bool else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return value
    }

    public func optBoolean(_ key: String) -> Bool? {
        try? getBoolean(key)
    }

    public func getString(_ key: String) throws -> String {
        guard let value = data[key] as? String else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return value
    }

    public func optString(_ key: String) -> String? {
        try? getString(key)
    }

    public func getDate(_ key: String) throws -> Date {
        guard let value = data[key] as? String else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        guard let date = Date.from(string: value, format: "\(Date.defaultFormat)'Z'", timeZone: TimeZone.utc) else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return date
    }

    public func optDate(_ key: String) -> Date? {
        try? getDate(key)
    }

    public func getTime(_ key: String) throws -> Time {
        guard let value = data[key] as? String else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        let components = value.split(separator: ":").compactMap { Int($0) }
        guard components.count == 3 else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return Time.of(hour: components[0], minute: components[1], second: components[2])
    }

    public func optTime(_ key: String) -> Time? {
        try? getTime(key)
    }

    public func getJSONObject(_ key: String) throws -> JSONObject {
        guard let value = data[key] as? [String: Any] else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return JSONObject(data: value)
    }

    public func optJSONObject(_ key: String) -> JSONObject? {
        try? getJSONObject(key)
    }

    public func getJSONArray(_ key: String) throws -> JSONArray {
        guard let value = data[key] as? [Any] else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return JSONArray(data: value)
    }

    public func optJSONArray(_ key: String) -> JSONArray? {
        try? getJSONArray(key)
    }

    public func getStringArray(_ key: String) throws -> [String] {
        guard let value = data[key] as? [String] else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return value
    }

    public func optStringArray(_ key: String) -> [String]? {
        try? getStringArray(key)
    }

    public func getIntArray(_ key: String) throws -> [Int] {
        guard let value = data[key] as? [Int] else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return value
    }

    public func getAnyArray(_ key: String) throws -> [Any] {
        guard let value = data[key] as? [Any] else {
            throw AnemoneException.jsonInvalidFormat(key: key)
        }
        return value
    }

    public func asMap() -> [String: Any] {
        data.filter { _, value in !(value is NSNull) }
    }

    public func setAny(_ key: String, _ value: Any?) {
        data[key] = value
    }

    public func setInt(_ key: String, _ value: Int?) {
        data[key] = value
    }

    public func setDouble(_ key: String, _ value: Double?) {
        data[key] = value
    }

    public func setDecimal(_ key: String, _ value: Decimal?) {
        data[key] = value
    }

    public func setString(_ key: String, _ value: String?) {
        data[key] = value
    }

    public func setBoolean(_ key: String, _ value: Bool?) {
        data[key] = value
    }

    public func setObject(_ key: String, _ value: JSONObject?) {
        data[key] = value?.data
    }

    public func setArray(_ key: String, _ value: [Any]?) {
        data[key] = value
    }

    public func setJSONArray(_ key: String, _ value: JSONArray?) {
        data[key] = value?.data
    }

    public func setStringArray(_ key: String, _ value: [String]?) {
        data[key] = value
    }
    
    public func setIntArray(_ key: String, _ value: [Int]?) {
        data[key] = value
    }

    public func setDate(_ key: String, _ value: Date?) {
        guard let value = value else {
            data[key] = nil
            return
        }

        data[key] = value.toString(format: Date.defaultFormat, timeZone: TimeZone.utc)
    }

    public func setTime(_ key: String, _ value: Time?) {
        guard let value = value else {
            data[key] = nil
            return
        }
        data[key] = value.description
    }

    public func toData() throws -> Data {
        guard let jsonData = try? JSONSerialization.data(withJSONObject: data, options: []) else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
        return jsonData
    }

    public func toString() throws -> String {
        let jsonData = try toData()
        guard let jsonString = String(data: jsonData, encoding: .utf8) else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
        return jsonString.replacingOccurrences(of: "\\/", with: "/")
    }
}
