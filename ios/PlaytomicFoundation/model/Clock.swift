//
//  Clock.swift
//  PlaytomicFoundation
//
//  Created by Ivan on 22/08/22.
//  Copyright © 2022 Playtomic. All rights reserved.
//

import Foundation

public protocol IClock {
    func now() -> Date
}

public extension IClock {
    func now() -> Date {
        Date()
    }
}

public struct Clock: IClock {
    private init() { }

    public static let standard = Clock()
}
