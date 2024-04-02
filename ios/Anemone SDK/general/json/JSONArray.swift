//
//  JSONArray.swift
//  Anemone SDK
//
//  Created by Angel Garcia on 17/01/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public class JSONArray {
    var data: [Any]

    public var count: Int {
        data.count
    }

    public init() {
        self.data = []
    }

    init(data: [Any]) {
        self.data = data.filter { !($0 is NSNull) }
    }

    public convenience init(objects: [JSONObject]) {
        self.init(data: objects.map { $0.data })
    }

    public convenience init(data: Data) throws {
        guard let json = try JSONSerialization.jsonObject(with: data, options: []) as? [Any] else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
        self.init(data: json)
    }

    public func getJSONObject(_ index: Int) throws -> JSONObject {
        guard index < data.count, let value = data[index] as? [String: Any] else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
        return JSONObject(data: value)
    }

    public func flatMap<T>(_ transform: (JSONObject) throws -> T?) rethrows -> [T] {
        try data.compactMap { item in
            guard let data = item as? [String: Any] else { return nil }
            return try transform(JSONObject(data: data))
        }
    }

    public func forEach(_ callback: (JSONObject) -> Void) throws {
        for index in 0 ..< data.count {
            let jsonObject = try getJSONObject(index)
            callback(jsonObject)
        }
    }

    public func asList<T>() throws -> [T] {
        guard let data = data as? [T] else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
        return data
    }

    public func asJSONList() -> [JSONObject] {
        data.compactMap { obj in
            guard let data = obj as? [String: Any] else { return nil }
            return JSONObject(data: data)
        }
    }

    public func add(_ object: JSONObject) {
        data.append(object.data)
    }

    public func addAll(_ objects: [JSONObject]) {
        data.append(contentsOf: objects.map { $0.data })
    }

    public func addInt(_ value: Int) {
        data.append(value)
    }

    public func addDouble(_ value: Double) {
        data.append(value)
    }

    public func addDecimal(_ value: Decimal) {
        data.append(value)
    }

    public func addString(_ value: String) {
        data.append(value)
    }

    public func addBoolean(_ value: Bool) {
        data.append(value)
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
