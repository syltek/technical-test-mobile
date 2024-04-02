//
//  HttpResponse.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 21/09/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct HttpResponse {
    public let request: HttpRequest
    public let headers: [String: String]
    public let code: Int
    public let body: Data

    public var isSuccessful: Bool {
        code >= 200 && code < 400
    }

    /**
     * Remember that you need to send <HttpRequest.includeTotalHeader(Key/Value)>
     * in the headers request to receive the total count.
     * You have a few examples:
     * <AnemoneSDK.MatchService.countMatches>
     * <AnemoneSDK.AcademyClassService.countCourses>
     * <AnemoneSDK.ActivityService.countTournaments>
     */
    public var totalCount: Int? {
        headers["total"].let { Int($0) }
    }
}

extension HttpResponse: Equatable { }
public func == (lhs: HttpResponse, rhs: HttpResponse) -> Bool {
    lhs.request == rhs.request &&
        lhs.headers == rhs.headers &&
        lhs.code == rhs.code &&
        lhs.body == rhs.body
}
