//
//  View+Conditionals.swift
//  PlaytomicUI
//
//  Created by Marta Tenés Ferrer on 3/6/22.
//  Copyright © 2022 Playtomic. All rights reserved.
//

import SwiftUI

public extension View {

    @ViewBuilder func `if`<Content: View>(_ condition: Bool, _ transform: (Self) -> Content) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }

    @ViewBuilder
    func `if`<V, Transform: View>(`let` value: V?, _ transform: (Self, V) -> Transform) -> some View {
        if let value = value {
            transform(self, value)
        } else {
            self
        }
    }

    @ViewBuilder func `if`<TrueContent: View, FalseContent: View>(_ condition: Bool, _ transform: (Self) -> TrueContent, `else`: (Self) -> FalseContent) -> some View {
        if condition {
            transform(self)
        } else {
            `else`(self)
        }
    }

    @ViewBuilder func `if`<V, TrueContent: View, FalseContent: View>(`let` value: V?, _ transform: (Self, V) -> TrueContent, `else`: (Self) -> FalseContent) -> some View {
        if let value = value {
            transform(self, value)
        } else {
            `else`(self)
        }
    }
}
