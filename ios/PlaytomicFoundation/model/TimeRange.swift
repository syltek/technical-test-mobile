//
//  TimeRange.swift
//  PlaytomicFoundation
//
//  Created by Angel Luis Garcia on 22/11/2018.
//  Copyright © 2018 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public struct TimeRange: Hashable, CustomStringConvertible {
    public let from: Time
    public let to: Time

    public var isFullDay: Bool {
        equivalent(TimeRange.fullDay())
    }

    public init(from: Time, to: Time) {
        self.from = from
        self.to = to
    }

    public init(_ rawValue: String) throws {
        let components = rawValue.components(separatedBy: "-")
        guard components.count == 2 else {
            throw NSError(domain: "Playtomic Foundation", code: 0, userInfo: [NSLocalizedDescriptionKey: "Wrong time range format for \(rawValue)"])
        }
        try self.init(from: Time(components[0]), to: Time(components[1]))
    }

    public var description: String {
        "\(from)-\(to)"
    }

    public static func fullDay() -> TimeRange {
        TimeRange(from: Time.first(), to: Time.last())
    }

    public func intersects(_ timeRange: TimeRange) -> Bool {
        timeRange.contains(self) || contains(timeRange) ||
            (to > timeRange.from && to < timeRange.to) ||
            (timeRange.to > from && timeRange.to < to)
    }

    public func contains(_ timeRange: TimeRange) -> Bool {
        from <= timeRange.from && to >= timeRange.to
    }

    public func contains(_ time: Time) -> Bool {
        from <= time && to >= time
    }

    // compares 2 timeranges ignoring some seconds difference
    public func equivalent(_ timeRange: TimeRange, maxSeconds: Int = 60) -> Bool {
        from.equivalent(timeRange.from, maxSeconds: maxSeconds) && to.equivalent(timeRange.to, maxSeconds: maxSeconds)
    }
}

public extension TimeRange {
    func toDateRange(referenceDate: Date = Date(), timeZone: TimeZone) -> DateRange {
        let fromDate = from.toDate(referenceDate: referenceDate, timeZone: timeZone)
        let toDate = to.toDate(referenceDate: referenceDate, timeZone: timeZone)
        return DateRange(from: fromDate, to: toDate < fromDate ? toDate.add(days: 1) : toDate)
    }

    func croppingTo() -> TimeRange {
        TimeRange(
            from: from,
            to: (from > to) ? Time.of(hour: 23, minute: 59, second: 59) : to
        )
    }

    func croppingFrom() -> TimeRange {
        TimeRange(
            from: (from > to) ? Time.of(hour: 00, minute: 00, second: 00) : from,
            to: to
        )
    }

    func excludingTo() -> TimeRange {
        TimeRange(from: from, to: to.removing(minutes: 1))
    }
}

