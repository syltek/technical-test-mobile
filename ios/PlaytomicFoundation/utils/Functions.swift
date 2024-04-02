//
//  Functions.swift
//  PlaytomicFoundation
//
//  Created by Angel Luis Garcia on 14/05/2019.
//  Copyright © 2019 Playtomic. All rights reserved.
//

import Foundation

public func synchronized(_ object: AnyObject, _ body: () throws -> Void) rethrows {
    objc_sync_enter(object)
    defer {
        objc_sync_exit(object)
    }
    try body()
}

public func synchronized<T>(_ object: AnyObject, _ body: () throws -> T) rethrows -> T {
    objc_sync_enter(object)
    defer {
        objc_sync_exit(object)
    }
    return try body()
}
