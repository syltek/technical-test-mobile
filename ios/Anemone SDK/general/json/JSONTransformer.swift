//
//  JSONTransformer.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 5/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public class JSONTransformer {
    public var rootKey: String?

    public init() { }

    public func transform<T: JSONMappable>(data: Data) -> T? {
        do {
            guard let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] else {
                return nil
            }

            let jsonObject: JSONObject
            if let rootKey = rootKey {
                jsonObject = try JSONObject(data: json).getJSONObject(rootKey)
            } else {
                jsonObject = JSONObject(data: json)
            }
            return instantiate(json: jsonObject)
        } catch {
            Log.w("JSONTransformer", "Can not transform object of type \(T.self) because \(error)")
            return nil
        }
    }

    public func transform<T: JSONMappable>(data: Data) -> [T]? {
        do {
            let json = try JSONSerialization.jsonObject(with: data, options: [])

            let jsonArray: [[String: Any]]?
            if let rootKey = rootKey {
                jsonArray = (json as? [String: Any])?[rootKey] as? [[String: Any]]
            } else {
                jsonArray = json as? [[String: Any]]
            }

            return jsonArray?.compactMap { instantiate(json: JSONObject(data: $0)) }
        } catch {
            Log.w("JSONTransformer", "Can not transform array of type \(T.self) because \(error)")
            return nil
        }
    }

    public func map<T: JSONMappable>(_ data: Data) -> Promise<T> {
        Promise(executeInBackground: true) { fulfill, reject in
            if let object: T = self.transform(data: data) {
                fulfill(object)
            } else {
                reject(AnemoneException.notMappable)
            }
        }
    }

    public func map<T: JSONMappable>(_ data: Data) -> Promise<[T]> {
        Promise(executeInBackground: true) { fulfill, reject in
            if let objects: [T] = self.transform(data: data) {
                fulfill(objects)
            } else {
                reject(AnemoneException.notMappable)
            }
        }
    }

    @inline(__always)
    private func instantiate<T: JSONMappable>(json: JSONObject) -> T? {
        do {
            return try T(json: json)
        } catch {
            Log.w("JSONTransformer", "Can not instantiate object of type \(T.self) because \(error)")
            return nil
        }
    }
}
