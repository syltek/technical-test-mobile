//
//  BookingType.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 26/09/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public enum BookingType: String {
    case `public` = "PUBLIC", restricted = "RESTRICTED", `private` = "PRIVATE"

    static func fromRawValue(_ rawValue: String) -> BookingType? {
        BookingType(rawValue: rawValue.uppercased())
    }
}

extension BookingType: CustomStringConvertible {
    public var description: String {
        rawValue
    }
}
