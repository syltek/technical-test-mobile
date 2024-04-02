//
//  HttpRequest.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 25/08/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct HttpRequest {
    public let method: HttpMethod
    public let url: String
    public let queryParams: [String: Any]?
    public let bodyParams: Any?
    public let headers: [String: String]?
    public let timeout: TimeInterval?

    private init(method: HttpMethod, url: String, queryParams: [String: Any]?, bodyParams: Any?, headers: [String: String]?, timeout: TimeInterval?) {
        self.method = method
        self.url = url
        self.queryParams = queryParams
        self.bodyParams = bodyParams
        self.headers = headers
        self.timeout = timeout
    }

    public func copy(
        method: HttpMethod? = nil,
        url: String? = nil,
        queryParams: [String: Any]? = nil,
        bodyParams: [String: Any]? = nil,
        headers: [String: String]? = nil,
        timeout: TimeInterval? = nil
    ) -> HttpRequest {
        HttpRequest(
            method: method ?? self.method,
            url: url ?? self.url,
            queryParams: queryParams ?? self.queryParams,
            bodyParams: bodyParams ?? self.bodyParams,
            headers: headers ?? self.headers,
            timeout: timeout ?? self.timeout
        )
    }

    public var contentType: String? {
        headers?["Content-Type"]
    }
}

public extension HttpRequest {
    static let includeTotalHeaderKey: String = "X-Playtomic-Include-Total-Header"
    static let includeTotalHeaderValue: String = "true"

    static func get(url: String, params: [String: Any]?, timeout: TimeInterval? = nil) -> HttpRequest {
        HttpRequest(method: .get, url: url, queryParams: params, bodyParams: nil, headers: nil, timeout: timeout)
    }

    static func post(url: String, params: Any?, timeout: TimeInterval? = nil, headers: [String: String]? = nil) -> HttpRequest {
        HttpRequest(method: .post, url: url, queryParams: nil, bodyParams: params, headers: headers, timeout: timeout)
    }

    static func post(
        url: String,
        multipartBodyParams: [MultipartBodyParam],
        multipartDataFiles: [MultipartDataFile],
        timeout: TimeInterval? = nil
    ) -> HttpRequest {
        let multipartBody = MultipartBody(
            multipartBodyParams: multipartBodyParams,
            files: multipartDataFiles
        )
        return HttpRequest(
            method: .post,
            url: url,
            queryParams: nil,
            bodyParams: multipartBody,
            headers: ["Content-Type": "multipart/form-data; boundary=\(multipartBody.boundary)"],
            timeout: timeout
        )
    }

    static func put(url: String, params: Any?, timeout: TimeInterval? = nil) -> HttpRequest {
        HttpRequest(method: .put, url: url, queryParams: nil, bodyParams: params, headers: nil, timeout: timeout)
    }
    
    static func put(
        url: String,
        multipartBodyParams: [MultipartBodyParam],
        multipartDataFiles: [MultipartDataFile],
        timeout: TimeInterval? = nil
    ) -> HttpRequest {
        let multipartBody = MultipartBody(
            multipartBodyParams: multipartBodyParams,
            files: multipartDataFiles
        )
        return HttpRequest(
            method: .put,
            url: url,
            queryParams: nil,
            bodyParams: multipartBody,
            headers: ["Content-Type": "multipart/form-data; boundary=\(multipartBody.boundary)"],
            timeout: timeout
        )
    }
    
    static func patch(
        url: String,
        multipartBodyParams: [MultipartBodyParam],
        multipartDataFiles: [MultipartDataFile],
        timeout: TimeInterval? = nil
    ) -> HttpRequest {
        let multipartBody = MultipartBody(
            multipartBodyParams: multipartBodyParams,
            files: multipartDataFiles
        )
        return HttpRequest(
            method: .patch,
            url: url,
            queryParams: nil,
            bodyParams: multipartBody,
            headers: ["Content-Type": "multipart/form-data; boundary=\(multipartBody.boundary)"],
            timeout: timeout
        )
    }

    static func patch(url: String, params: Any?, timeout: TimeInterval? = nil) -> HttpRequest {
        HttpRequest(method: .patch, url: url, queryParams: nil, bodyParams: params, headers: nil, timeout: timeout)
    }

    static func delete(url: String, params: [String: Any]?, timeout: TimeInterval? = nil) -> HttpRequest {
        HttpRequest(method: .delete, url: url, queryParams: params, bodyParams: nil, headers: nil, timeout: timeout)
    }

    static func head(url: String, params: [String: Any]?, headers: [String: String]? = nil, timeout: TimeInterval? = nil) -> HttpRequest {
        HttpRequest(method: HttpMethod.head, url: url, queryParams: params, bodyParams: nil, headers: headers, timeout: timeout)
    }
}

extension HttpRequest: Equatable { }
public func == (lhs: HttpRequest, rhs: HttpRequest) -> Bool {
    let lhsBody = (lhs.bodyParams as? [String: Any])?.description
        ?? (lhs.bodyParams as? [[String: Any]])?.description
        ?? (lhs.bodyParams as? MultipartBody)?.boundary
    let rhsBody = (rhs.bodyParams as? [String: Any])?.description
        ?? (rhs.bodyParams as? [[String: Any]])?.description
        ?? (rhs.bodyParams as? MultipartBody)?.boundary
    return lhs.method == rhs.method &&
        lhs.url == rhs.url &&
        lhs.queryParams?.description == rhs.queryParams?.description &&
        lhsBody == rhsBody &&
        (lhs.headers ?? [:]) == (rhs.headers ?? [:]) &&
        lhs.timeout == rhs.timeout
}
