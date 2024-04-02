//
//  PlaytomicApp.swift
//  Playtomic
//
//  Created by Angel Luis Garcia on 2/4/24.
//

import UIKit

@UIApplicationMain class PlaytomicAppDelegate: UIResponder, UIApplicationDelegate {
    let dependencyProvider = DependencyProvider()
    var window: UIWindow?

    func application(_: UIApplication, didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        window = UIWindow()
        window?.rootViewController = UINavigationController(rootViewController: dependencyProvider.viewControllers.rootViewController)
        window?.makeKeyAndVisible()

        return true
    }
}
