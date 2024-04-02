//
//  PlaytomicViewController.swift
//  PlaytomicUI
//
//  Created by Angel Luis Garcia on 22/01/2019.
//  Copyright © 2019 Playtomic. All rights reserved.
//

import Foundation
import SwiftUI
import UIKit

open class PlaytomicViewController: UIViewController {
    public var lifecycle = ControllerLifeCycle()

    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        lifecycle.isPresented = true
    }

    override open func viewWillDisappear(_ animated: Bool) {
        lifecycle.isPresented = false
        super.viewWillDisappear(animated)
    }

}
