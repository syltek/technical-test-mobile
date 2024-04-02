//
//  HttpClient.swift
//  Anemone SDK
//
//  Created by Angel Garcia on 20/12/2016.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation
import UIKit

public class HttpClient: IHttpClient {
    let urlSession: URLSession
    let baseUrl: String
    let urlEncoder: IHttpParameterEncoder
    let bodyEncoders: [IHttpParameterEncoder]

    public convenience init(
        baseUrl: String,
        timeOut: TimeInterval? = nil,
        urlEncoder: IHttpParameterEncoder? = nil,
        bodyEncoders: [IHttpParameterEncoder]? = nil,
        sessionDelegate: URLSessionDelegate? = nil
    ) {
        let config = URLSessionConfiguration.default.apply { config in
            if let timeOut = timeOut {
                config.timeoutIntervalForRequest = timeOut
            }
        }
        self.init(
            baseUrl: baseUrl,
            urlSession: URLSession(
                configuration: config,
                delegate: sessionDelegate,
                delegateQueue: nil
            ),
            urlEncoder: urlEncoder,
            bodyEncoders: bodyEncoders
        )
    }

    public init(
        baseUrl: String,
        urlSession: URLSession,
        urlEncoder: IHttpParameterEncoder? = nil,
        bodyEncoders: [IHttpParameterEncoder]? = nil
    ) {
        self.baseUrl = baseUrl
        self.urlSession = urlSession
        self.urlEncoder = urlEncoder ?? HttpUrlParameterEncoder()
        self.bodyEncoders = bodyEncoders ??  [
            HttpJsonParameterEncoder(),
            HttpUrlParameterEncoder(),
            HttpMultipartParameterEncoder(jsonEncoder: HttpJsonParameterEncoder())
        ]
    }

    public func request(_ httpRequest: HttpRequest) -> Promise<HttpResponse> {
        let request: URLRequest
        do {
            request = try buildURLRequest(httpRequest: httpRequest)
        } catch {
            return Promise(error: error)
        }
        Log.d(
            "HttpClient",
            " --->  🚀 \(request.httpMethod ?? "") \(request.url?.absoluteString ?? "")" +
            "\n\t\(request.httpBody.let { String(data: $0, encoding: .utf8) } ?? "")"
        )
        return Promise { fulfill, reject in
            let task = self.urlSession.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
                guard let response = response, (error as NSError?)?.code != NSURLErrorTimedOut else {
                    Log.d("HttpClient", " <--- ❗️ \(request.httpMethod ?? "") \(request.url?.absoluteString ?? "")\n\tERROR - TIMEOUT")
                    reject(AnemoneException.timeout)
                    return
                }
                let httpResponse = HttpResponse(request: httpRequest, response: response, body: data)

                if error == nil && httpResponse.isSuccessful {
                    Log.d(
                        "HttpClient",
                        " <--- ✅ \(request.httpMethod ?? "") \(request.url?.absoluteString ?? "")" +
                            "\n\t\(httpResponse.code)\n\t\(String(data: data ?? Data(), encoding: .utf8) ?? "-")"
                    )
                    fulfill(httpResponse)
                } else {
                    var serverMessage: Message?
                    if let data = data, data.count > 0 {
                        serverMessage = self.getErrorMessage(data: data)
                    }
                    Log.d(
                        "HttpClient",
                        " <--- ❗️ \(request.httpMethod ?? "") \(request.url?.absoluteString ?? "")" +
                            "\n\t\(httpResponse.code)\n\t\(String(data: data ?? Data(), encoding: .utf8) ?? "-")"
                    )
                    reject(AnemoneException.network(response: httpResponse, error: error, serverMessage: serverMessage))
                }
            }
            task.resume()
        }
    }

    func buildURLRequest(httpRequest: HttpRequest) throws -> URLRequest {
        var urlString = hasScheme(httpRequest.url) ? httpRequest.url : "\(baseUrl)\(httpRequest.url)"
        var body: Data?
        do {
            body = try buildRequestBody(httpRequest)
            try buildRequestQuery(httpRequest).let {
                urlString += "?\($0)"
            }
        } catch {
            Log.w("HttpClient", " <--- ❌ Error creating request body for \(urlString)\n\(error)")
            throw error
        }
        guard let url = URL(string: urlString), url.scheme != nil else {
            Log.w("HttpClient", " <--- ❌ Error creating url for \(urlString)")
            throw AnemoneException.wrongUrl
        }
        var request = URLRequest(url: url)
        request.timeoutInterval = httpRequest.timeout ?? urlSession.configuration.timeoutIntervalForRequest
        request.httpMethod = httpRequest.method.rawValue
        request.httpBody = body
        request.setValue(encoderFor(httpRequest: httpRequest)?.contentType(), forHTTPHeaderField: "Content-Type")
        httpRequest.headers?.forEach { request.setValue($0.value, forHTTPHeaderField: $0.key) }
        return request
    }

    func buildRequestQuery(_ request: HttpRequest) throws -> String? {
        guard let params = request.queryParams else { return nil }
        return try urlEncoder.stringEncode(params)
    }

    func buildRequestBody(_ request: HttpRequest) throws -> Data? {
        guard request.method != .get && request.method != .delete else { return nil }
        guard let params = request.bodyParams else { return nil }
        guard let encoder = encoderFor(httpRequest: request) else {
            throw AnemoneException.jsonInvalidFormat(key: nil)
        }
        return try encoder.dataEncode(params)
    }

    func getErrorMessage(data: Data) -> Message? {
        JSONTransformer().transform(data: data)
    }

    func hasScheme(_ endpoint: String) -> Bool {
        endpoint.hasPrefix("http://") || endpoint.hasPrefix("https://")
    }

    func encoderFor(httpRequest: HttpRequest) -> IHttpParameterEncoder? {
        guard let type = httpRequest.contentType else { return bodyEncoders.first }
        return bodyEncoders.filter { $0.contentType(request: httpRequest).starts(with: type) }.first
    }
}

private extension HttpResponse {
    init(request: HttpRequest, response: URLResponse, body: Data?) {
        let httpResponse = response as? HTTPURLResponse
        self.init(
            request: request,
            headers: httpResponse?.allHeaderFields as? [String: String] ?? [:],
            code: httpResponse?.statusCode ?? 0,
            body: body ?? Data()
        )
    }
}
