//
//  LocalHttpClient.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 5/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public class LocalHttpClient: IHttpClient {
    public init() { }

    public func request(_ httpRequest: HttpRequest) -> Promise<HttpResponse> {
        Promise(executeInBackground: true) { fulfill, reject in
            let resource = "assets/\(httpRequest.url)"

            if
                let path = Bundle(for: type(of: self)).path(forResource: resource, ofType: "json"),
                let data = try? Data(contentsOf: URL(fileURLWithPath: path))
            {
                fulfill(HttpResponse(request: httpRequest, headers: [:], code: 200, body: data))
            } else {
                reject(
                    AnemoneException.network(
                        response: HttpResponse(request: httpRequest, headers: [:], code: 404, body: Data()),
                        error: AnemoneException.notFound,
                        serverMessage: nil
                    )
                )
            }
        }
    }
}
