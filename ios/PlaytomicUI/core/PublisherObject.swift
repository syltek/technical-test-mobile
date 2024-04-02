//
//  PublisherObject.swift
//  PlaytomicUI
//
//  Created by Ivan on 06/04/23.
//  Copyright © 2023 Playtomic. All rights reserved.
//

import Foundation

public class PublisherObject<T>: ObservableObject {
    @Published public var obj: T?
    
    public init(obj: T?) {
        self.obj = obj
    }
}
