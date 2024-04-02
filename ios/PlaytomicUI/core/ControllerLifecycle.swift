//
//  ControllerLifecycle.swift
//  Mozart
//
//  Created by Angel Luis Garcia on 24/2/22.
//  Copyright © 2022 Playtomic. All rights reserved.
//

import Combine
import Foundation
import SwiftUI

private struct ControllerLifeCycleKey: EnvironmentKey {
    static let defaultValue = ControllerLifeCycle()
}

public extension EnvironmentValues {
    var controllerLifeCycle: ControllerLifeCycle {
        get { self[ControllerLifeCycleKey.self] }
        set { self[ControllerLifeCycleKey.self] = newValue }
    }
}

public class ControllerLifeCycle: ObservableObject {
    @Published public var isPresented: Bool = false
}

public extension View {
    @inlinable
    func onControllerAppear(perform action: @escaping (() -> Void)) -> some View {
        modifier(ControllerLifeCycleModifier(appearAction: action))
    }
}

public extension View {
    @inlinable
    func onControllerDisappear(perform action: @escaping (() -> Void)) -> some View {
        modifier(ControllerLifeCycleModifier(disappearAction: action))
    }
}

public struct ControllerLifeCycleModifier: ViewModifier {
    @Environment(\.controllerLifeCycle) var controllerLifeCycle
    @State var isPresented: Bool?
    let appearAction: (() -> Void)?
    let disappearAction: (() -> Void)?

    public init(appearAction: (() -> Void)? = nil, disappearAction: (() -> Void)? = nil) {
        self.appearAction = appearAction
        self.disappearAction = disappearAction
    }

    public func body(content: Content) -> some View {
        content
            .onAppear() // https://stackoverflow.com/a/61200321/378433
            .onReceive(controllerLifeCycle.$isPresented) { newValue in
                guard newValue != isPresented else { return }
                isPresented = newValue
                if newValue {
                    appearAction?()
                } else {
                    disappearAction?()
                }
            }
    }
}
