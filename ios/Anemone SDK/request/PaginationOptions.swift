//
//  PaginationOptions.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 24/03/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct PaginationOptions: Equatable {
    public var page: Int?
    public var size: Int?

    public init() { }

    public func withPage(_ page: Int?) -> PaginationOptions {
        var options = self
        options.page = page
        return options
    }

    public func withSize(_ size: Int?) -> PaginationOptions {
        var options = self
        options.size = size
        return options
    }

    var params: [String: Any] {
        var params = [String: Any]()
        if let page = page {
            params["page"] = page
        }
        if let size = size {
            params["size"] = size
        }
        return params
    }
}
