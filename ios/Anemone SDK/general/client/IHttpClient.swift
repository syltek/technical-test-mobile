//
//  IHttpClient.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 5/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public protocol IHttpClient {
    func request(_ httpRequest: HttpRequest) -> Promise<HttpResponse>
}

public extension IHttpClient {
    func get(endpoint: String, params: [String: Any]?) -> Promise<Data> {
        request(HttpRequest.get(url: endpoint, params: params))
            .then { $0.body }
    }

    func post(endpoint: String, params: Any?) -> Promise<Data> {
        request(HttpRequest.post(url: endpoint, params: params))
            .then { $0.body }
    }

    func post(endpoint: String, multipartBodyParams: [MultipartBodyParam], multipartDataFiles: [MultipartDataFile]) -> Promise<Data> {
        request(HttpRequest.post(url: endpoint, multipartBodyParams: multipartBodyParams, multipartDataFiles: multipartDataFiles))
            .then { $0.body }
    }

    func put(endpoint: String, params: Any?) -> Promise<Data> {
        request(HttpRequest.put(url: endpoint, params: params))
            .then { $0.body }
    }
    
    func put(endpoint: String, multipartBodyParams: [MultipartBodyParam], multipartDataFiles: [MultipartDataFile]) -> Promise<Data> {
        request(HttpRequest.put(url: endpoint, multipartBodyParams: multipartBodyParams, multipartDataFiles: multipartDataFiles))
            .then { $0.body }
    }
    
    func patch(endpoint: String, multipartBodyParams: [MultipartBodyParam], multipartDataFiles: [MultipartDataFile]) -> Promise<Data> {
        request(HttpRequest.patch(url: endpoint, multipartBodyParams: multipartBodyParams, multipartDataFiles: multipartDataFiles))
            .then { $0.body }
    }

    func patch(endpoint: String, params: Any?) -> Promise<Data> {
        request(HttpRequest.patch(url: endpoint, params: params))
            .then { $0.body }
    }

    func delete(endpoint: String, params: [String: Any]?) -> Promise<Data> {
        request(HttpRequest.delete(url: endpoint, params: params))
            .then { $0.body }
    }
}
