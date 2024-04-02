//
//  Log.swift
//  PlaytomicFoundation
//
//  Created by Angel Luis Garcia on 10/8/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//
// swiftlint:disable opening_brace

import Foundation
import os

public enum Log {
    public enum Level: Int { case error = 0, warn = 1, info = 2, debug = 3 }

    public static var level: Level = .warn

    public static var errorHandlers: [(_ tag: String, _ message: String) -> Void] = [
        { tag, message in logger(tag: tag).error("\(message)") }
    ]
    public static var warningHandlers: [(_ tag: String, _ message: String) -> Void] = [
        { tag, message in logger(tag: tag).warning("\(message)") }
    ]
    public static var infoHandlers: [(_ tag: String, _ message: String) -> Void] = [
        { tag, message in logger(tag: tag).info("\(message)") }
    ]

    public static func e(_ tag: String, _ messageHandler: @autoclosure () -> String) {
        guard level.rawValue >= Level.error.rawValue else { return }
        let message = messageHandler()
        errorHandlers.forEach { $0(tag, message) }
    }

    public static func w(_ tag: String, _ messageHandler: @autoclosure () -> String) {
        guard level.rawValue >= Level.warn.rawValue else { return }
        let message = messageHandler()
        warningHandlers.forEach { $0(tag, message) }
    }

    public static func i(_ tag: String, _ messageHandler: @autoclosure () -> String) {
        guard level.rawValue >= Level.info.rawValue else { return }
        let message = messageHandler()
        infoHandlers.forEach { $0(tag, message) }
    }

    public static func d(_ tag: String, _ messageHandler: @autoclosure () -> String) {
        guard level.rawValue >= Level.debug.rawValue else { return }
        let message = messageHandler()
        logger(tag: tag).debug("\(message)")
    }

    private static func logger(tag: String) -> Logger {
        Logger(subsystem: Bundle.main.bundleIdentifier ?? "PlaytomicFoundation", category: tag)
    }
}
