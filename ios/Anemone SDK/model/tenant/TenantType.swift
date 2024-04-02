//
//  TenantType.swift
//  Anemone SDK
//
//  Created by Manuel González Villegas on 26/6/18.
//  Copyright © 2018 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public enum TenantType: String {
    case syltekcrm
    case playtomicIntegrated = "playtomic_integrated"
    case anemoneOnly = "anemone"

    static func fromRawValue(_ rawValue: String) -> TenantType? {
        TenantType(rawValue: rawValue.lowercased())
    }
}

extension TenantType: CustomStringConvertible {
    public var description: String {
        rawValue
    }
}
