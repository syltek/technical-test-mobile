//
//  Scope+Utils.swift
//  PlaytomicFoundation
//
//  Created by Cecilia on 27/05/22.
//  Copyright © 2022 Playtomic. All rights reserved.
//

import Foundation

public protocol HasApply: AnyObject { }

public extension HasApply {
    @discardableResult
    @inlinable
    func apply(_ closure: (Self) -> Void) -> Self {
        closure(self)
        return self
    }
}

extension NSObject: HasApply { }
