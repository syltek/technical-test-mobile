//
//  UIHostingViewControllerCustom.swift
//  SwiftUI
//
//  Created by Miguel Olmedo on 24/12/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//
// swiftlint:disable overridden_super_call

import SwiftUI

/// Extension to View protocol that allows you to convert a SwiftUI view into a UIKit view controller.
/// It adds lifecycle management and the usage of a custom HostingController containing some fixes
/// for navigation and layout
public extension View {
    func toHostingController() -> UIHostingController<some View> {
        let lifecycle = ControllerLifeCycle()
        let vc = UIHostingControllerCustom(
            rootView: environment(\.controllerLifeCycle, lifecycle)
        )
        vc.lifecycle = lifecycle
        return vc
    }
}

private class UIHostingControllerCustom<YourView: View>: UIHostingController<YourView> {
    var lifecycle = ControllerLifeCycle()

    override public func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        lifecycle.isPresented = true
    }

    override open func viewWillDisappear(_ animated: Bool) {
        lifecycle.isPresented = false
        super.viewWillDisappear(animated)
    }

    override public func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        view.invalidateIntrinsicContentSize()
    }
}
