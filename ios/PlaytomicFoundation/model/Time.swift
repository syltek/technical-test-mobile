//
//  Time.swift
//  PlaytomicFoundation
//
//  Created by Angel Luis Garcia on 22/11/2018.
//  Copyright © 2018 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct Time: Equatable, Hashable, CustomStringConvertible {
    public let hour: Int
    public let minute: Int
    public let second: Int

    private init(
        hour: Int,
        minute: Int,
        second: Int
    ) {
        self.hour = hour
        self.minute = minute
        self.second = second
    }

    public init(_ rawValue: String) throws {
        let components = rawValue.components(separatedBy: ":")
        if
            components.count == 2,
            let hour = Int(components[0]),
            let minute = Int(components[1])
        {
            self.init(hour: hour, minute: minute, second: 0)
        } else if
            components.count == 3,
            let hour = Int(components[0]),
            let minute = Int(components[1]),
            let second = Int(components[2])
        {
            self.init(hour: hour, minute: minute, second: second)
        } else {
            throw NSError(domain: "Playtomic Foundation", code: 0, userInfo: [NSLocalizedDescriptionKey: "Wrong time format for \(rawValue)"])
        }
    }

    public static func of(hour: Int = 0, minute: Int = 0, second: Int = 0) -> Time {
        Time(hour: hour, minute: minute, second: second)
    }

    public static func now() -> Time {
        let components = DateComponents()
        return Time(hour: components.hour ?? 0, minute: components.minute ?? 0, second: components.second ?? 0)
    }

    public static func first() -> Time {
        Time.of()
    }

    public static func last() -> Time {
        Time.of(hour: 23, minute: 59, second: 59)
    }

    public static func dayHours() -> [Time] {
        (0 ... 23).map { Time.of(hour: $0) }
    }

    public var description: String {
        String(format: "%02d:%02d:%02d", hour, minute, second)
    }

    public func adding(minutes: Int) -> Time {
        var hour = hour + minutes / 60
        var minutes = minute + (minutes % 60)
        if minutes >= 60 {
            hour += 1
            minutes -= 60
        }
        return Time(hour: hour, minute: minutes, second: second)
    }

    public func removing(minutes: Int) -> Time {
        var hour = hour - (minutes / 60)
        var minutes = minute - (minutes % 60)

        if minutes < 0 {
            hour -= 1
            minutes = 60 + minutes
        }

        return Time(hour: hour, minute: minutes, second: second)
    }

    public func adding(hours: Int) -> Time {
        Time(hour: (hour + hours) % 24, minute: minute, second: second)
    }

    // compares 2 times ignoring some seconds difference
    public func equivalent(_ time: Time, maxSeconds: Int = 60) -> Bool {
        abs(toDaySeconds() - time.toDaySeconds()) <= maxSeconds
    }

    private func toDaySeconds() -> Int {
        hour * 3600 + minute * 60 + second
    }
}

public extension Time {
    static func generateTimes(
        fromTime: Time = Time.of(),
        toTime: Time = Time.of(hour: 23, minute: 59, second: 59),
        minutesInterval: Int
    ) -> [Time] {
        var times: [Time] = []
        var lastTime = fromTime
        repeat {
            times.append(lastTime)
            lastTime = lastTime.adding(minutes: minutesInterval)
        } while lastTime <= toTime
        return times
    }

    func toDate(referenceDate: Date = Date(), timeZone: TimeZone) -> Date {
        let calendar = NSCalendar.current
        var timeComponents = calendar.dateComponents(in: timeZone, from: referenceDate)

        timeComponents.setValue(hour, for: .hour)
        timeComponents.setValue(minute, for: .minute)
        timeComponents.setValue(second, for: .second)
        timeComponents.setValue(0, for: .nanosecond)

        let date: Date! = calendar.date(from: timeComponents)
        return date
    }
}

extension Time: Comparable {
    public static func < (lhs: Time, rhs: Time) -> Bool {
        lhs.toDaySeconds() < rhs.toDaySeconds()
    }
}
