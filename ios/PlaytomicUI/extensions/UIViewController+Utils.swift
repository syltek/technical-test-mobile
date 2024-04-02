//
//  UIViewController+Utils.swift
//  PlaytomicUI
//
//  Created by Angel Luis Garcia on 8/4/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//

import Foundation
import SwiftUI
import UIKit

public extension UIViewController {

    func attachChild(viewController: UIViewController, container: UIView) {
        addChild(viewController)
        viewController.view.translatesAutoresizingMaskIntoConstraints = false
        container.addSubview(viewController.view)
        viewController.view.topAnchor.constraint(equalTo: container.topAnchor).isActive = true
        viewController.view.bottomAnchor.constraint(equalTo: container.bottomAnchor).isActive = true
        viewController.view.trailingAnchor.constraint(equalTo: container.trailingAnchor).isActive = true
        viewController.view.leadingAnchor.constraint(equalTo: container.leadingAnchor).isActive = true
        viewController.didMove(toParent: self)
    }

    func detachChild(viewController: UIViewController) {
        viewController.willMove(toParent: nil)
        viewController.view.removeFromSuperview()
        viewController.removeFromParent()
    }

}
