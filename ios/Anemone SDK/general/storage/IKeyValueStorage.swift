//
//  IKeyValueStorage.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 5/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public protocol IKeyValueStorage: AnyObject {
    func string(name: String) -> String?

    func setString(name: String, value: String?)

    func bool(name: String) -> Bool?

    func setBool(name: String, value: Bool?)

    func int(name: String) -> Int?

    func setInt(name: String, value: Int?)

    func double(name: String) -> Double?

    func setDouble(name: String, value: Double?)

    func data(name: String) -> Data?

    func setData(name: String, value: Data?)
}

public extension IKeyValueStorage {
    func date(name: String) -> Date? {
        guard let value = double(name: name) else { return nil }
        return Date(timeIntervalSince1970: value)
    }

    func setDate(name: String, value: Date?) {
        setDouble(name: name, value: value?.timeIntervalSince1970)
    }

    func object<T: JSONMappable>(name: String) -> T? {
        guard
            let jsonData = data(name: name),
            let json = try? JSONObject(data: jsonData)
        else { return nil }
        return try? T(json: json)
    }

    func setObject<T: JSONSerializable>(name: String, value: T?) {
        setData(name: name, value: try? value?.toJson().toData())
    }
}
