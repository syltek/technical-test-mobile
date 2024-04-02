//
//  MultipartBody.swift
//  Anemone SDK
//
//  Created by Manuel González Villegas on 28/5/21.
//  Copyright © 2021 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct MultipartBody {
    let boundary: String
    public let multipartBodyParams: [MultipartBodyParam]
    public let files: [MultipartDataFile]

    public init(
        multipartBodyParams: [MultipartBodyParam],
        files: [MultipartDataFile]
    ) {
        self.init(
            boundary: "Boundary-\(UUID().uuidString.replacingOccurrences(of: "-", with: ""))",
            multipartBodyParams: multipartBodyParams,
            files: files
        )
    }

    private init(
        boundary: String,
        multipartBodyParams: [MultipartBodyParam],
        files: [MultipartDataFile]
    ) {
        self.boundary = boundary
        self.multipartBodyParams = multipartBodyParams
        self.files = files
    }
}

extension MultipartBody: Equatable { }
public func == (lhs: MultipartBody, rhs: MultipartBody) -> Bool {
    lhs.boundary == rhs.boundary
}
