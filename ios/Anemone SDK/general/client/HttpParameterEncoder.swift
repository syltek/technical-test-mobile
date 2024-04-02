//
//  HttpParameterEncoder.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 09/10/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//
// swiftlint:disable force_cast
// swiftlint:disable syntactic_sugar

import Foundation

public protocol IHttpParameterEncoder {
    func contentType(request: HttpRequest?) -> String
    func stringEncode(_ params: [String: Any]) throws -> String
    func dataEncode(_ params: Any) throws -> Data
}

public extension IHttpParameterEncoder {
    func contentType(request: HttpRequest? = nil) -> String {
        contentType(request: request)
    }
}

public class HttpUrlParameterEncoder: IHttpParameterEncoder {
    public init() { }

    public func contentType(request _: HttpRequest?) -> String {
        "application/x-www-form-urlencoded"
    }

    public func stringEncode(_ params: [String: Any]) throws -> String {
        try urlEncoded(params)
    }

    public func dataEncode(_ params: Any) throws -> Data {
        if let params = params as? [String: Any] {
            return try urlEncoded(params).data(using: .utf8) ?? Data()
        } else if let params = params as? [[String: Any]] {
            let flatParams = params.flatMap { $0 }.reduce([String: Any]()) { dict, tuple in
                var dict = dict
                dict.updateValue(tuple.1, forKey: tuple.0)
                return dict
            }
            return try urlEncoded(flatParams).data(using: .utf8) ?? Data()
        } else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
    }

    private func urlEncoded(_ params: [String: Any]) throws -> String {
        let encodedParameters = try params.keys.sorted().compactMap { key -> String? in
            guard let value = params[key] else { return nil }
            if case Optional<Any>.none = value { return nil }
            let encoded = try self.urlEncoded(value: value)
            return "\(key)=\(encoded)"
        }
        return encodedParameters.joined(separator: "&")
    }

    func urlEncoded(value: Any) throws -> String {
        if let arrayValue = value as? [Any] {
            return try arrayValue.map(urlEncoded).joined(separator: ",")
        }
        if let date = value as? Date {
            return date.toString(format: Date.defaultFormat, timeZone: TimeZone.utc)
        }
        if let data = value as? Data {
            return try urlEncoded(value: data.base64EncodedString())
        }
        if let stringValue = value as? CustomStringConvertible {
            let allowedCharacterSet = (CharacterSet(charactersIn: "!*'();:@&=+$,/?%#[]| ").inverted)
            if
                let encoded = stringValue
                    .description
                    .addingPercentEncoding(withAllowedCharacters: allowedCharacterSet)
            {
                return encoded
            }
        }

        throw AnemoneException.jsonInvalidFormat(key: nil)
    }
}

public class HttpJsonParameterEncoder: IHttpParameterEncoder {
    public init() { }

    public func contentType(request _: HttpRequest?) -> String {
        "application/json"
    }

    public func stringEncode(_ params: [String: Any]) throws -> String {
        String(data: try dataEncode(params), encoding: .utf8) ?? ""
    }

    public func dataEncode(_ params: Any) throws -> Data {
        if let params = params as? [String: Any] {
            return try jsonEncoded(params).toData()
        } else if let params = params as? [Any] {
            return try jsonEncoded(params).toData()
        } else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
    }

    // swiftlint:disable cyclomatic_complexity syntactic_sugar
    private func jsonEncoded(_ params: [String: Any]) throws -> JSONObject {
        let json = JSONObject()
        try params.forEach { key, value in
            if case Optional<Any>.none = value {
                Log.d("HttpParameterEncoder", "Skipping field \(key) with null value")
            } else {
                switch value {
                case is JSONSerializable: json.setObject(key, (value as! JSONSerializable).toJson())
                case is Int: json.setInt(key, value as? Int)
                case is Double: json.setDouble(key, value as? Double)
                case is Float: json.setDouble(key, Double(value as! Float))
                case is Decimal: json.setDecimal(key, value as? Decimal)
                case is Bool: json.setBoolean(key, value as? Bool)
                case is String: json.setString(key, value as? String)
                case is Date: json.setDate(key, value as? Date)
                case is Data: json.setString(key, (value as? Data)?.base64EncodedString())
                case is [String: Any]: json.setObject(key, try jsonEncoded(value as! [String: Any]))
                case is [Any]: json.setJSONArray(key, try jsonEncoded(value as! [Any]))
                case is CustomStringConvertible: json.setString(key, (value as! CustomStringConvertible).description)
                default: throw AnemoneException.jsonInvalidFormat(key: key)
                }
            }
        }
        return json
    }

    private func jsonEncoded(_ array: [Any]) throws -> JSONArray {
        let json = JSONArray()
        try array.forEach { value in
            switch value {
            case is JSONSerializable: json.add((value as! JSONSerializable).toJson())
            case is JSONObject: json.add(value as! JSONObject)
            case is Int: json.addInt(value as! Int)
            case is Double: json.addDouble(value as! Double)
            case is Float: json.addDouble(Double(value as! Float))
            case is Decimal: json.addDecimal(value as! Decimal)
            case is Bool: json.addBoolean(value as! Bool)
            case is String: json.addString(value as! String)
            case is [String: Any]: json.add(try jsonEncoded(value as! [String: Any]))
            case is CustomStringConvertible: json.addString((value as! CustomStringConvertible).description)
            default: throw AnemoneException.jsonInvalidFormat(key: nil)
            }
        }
        return json
    }
    // swiftlint:enable cyclomatic_complexity syntactic_sugar
}

public class HttpMultipartParameterEncoder: IHttpParameterEncoder {
    private let jsonEncoder: IHttpParameterEncoder
    public init(jsonEncoder: IHttpParameterEncoder) {
        self.jsonEncoder = jsonEncoder
    }

    public func contentType(request: HttpRequest?) -> String {
        let boundary: String? = (request?.bodyParams as? MultipartBody).let { "boundary=\($0.boundary)" }
        return ["multipart/form-data", boundary].compactMap { $0 }.joined(separator: "; ")
    }

    public func stringEncode(_ params: [String: Any]) throws -> String {
        try jsonEncoder.stringEncode(params)
    }

    public func dataEncode(_ params: Any) throws -> Data {
        guard let multipartBody = params as? MultipartBody else {
            throw AnemoneException.notMappable
        }
        let bodysData = try multipartBody.multipartBodyParams.map { try self.bodyEncode($0, boundary: multipartBody.boundary) }
        let filesData = try multipartBody.files.map { try self.fileEncode($0, boundary: multipartBody.boundary) }
        guard let endData = "--\(multipartBody.boundary)--\r\n".data(using: .utf8) else {
            throw AnemoneException.notMappable
        }

        return [bodysData, filesData, [endData]].flatten().reduce(.init(), +)
    }

    private func bodyEncode(_ bodyParam: MultipartBodyParam, boundary: String) throws -> Data {
        let encodedBody = try jsonEncoder.dataEncode(bodyParam.params)
        guard
            let body = String(data: encodedBody, encoding: .utf8),
            let data = """
            --\(boundary)
            Content-Disposition: form-data; name="\(bodyParam.name)"
            Content-Type: application/json

            \(body)

            """.replacingOccurrences(of: "\n", with: "\r\n").data(using: .utf8)
        else { throw AnemoneException.notMappable }
        return data
    }

    private func fileEncode(_ file: MultipartDataFile, boundary: String) throws -> Data {
        let data = NSMutableData()

        data.appendString("--\(boundary)\r\n")
        data.appendString("Content-Disposition: form-data; name=\"\(file.name)\"; filename=\"\(file.fileName)\(file.extensionFileName)\"\r\n")
        data.appendString("Content-Type: \(file.contentType)\r\n\r\n")
        data.append(file.data)
        data.appendString("\r\n")

        return data as Data
    }
}

private extension NSMutableData {
    func appendString(_ string: String) {
        if let data = string.data(using: .utf8) {
            append(data)
        }
    }
}
