//
//  DateRange.swift
//  PlaytomicFoundation
//
//  Created by Angel Luis Garcia on 15/03/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public class DateRange {
    public let from: Date
    public let to: Date

    public init(from: Date, to: Date) {
        self.from = from
        self.to = to
    }

    public init(at date: Date, spanMinutes: Int) {
        self.from = date.add(minutes: -spanMinutes)
        self.to = date.add(minutes: spanMinutes)
    }

    public var spanMinutes: Int {
        Int(round((to.timeIntervalSinceReferenceDate - from.timeIntervalSinceReferenceDate) / (2.0 * 60.0)))
    }

    public func contains(date: Date) -> Bool {
        from <= date && date <= to
    }

    public func allDates(spanMinutes: Int, includeLast: Bool = false) -> [Date] {
        var dates = [Date]()
        var lastDate = from
        while lastDate < to {
            dates.append(lastDate)
            lastDate = lastDate.add(minutes: spanMinutes)
        }
        if includeLast && lastDate == to {
            dates.append(lastDate)
        }
        return dates
    }

    public func adding(minutes: Int) -> DateRange {
        DateRange(from: from.add(minutes: minutes), to: to.add(minutes: minutes))
    }

    public func adding(days: Int) -> DateRange {
        DateRange(from: from.add(days: days), to: to.add(days: days))
    }
}

public class TimeZoneDateRange: DateRange {
    public let timeZone: TimeZone

    public init(from: Date, to: Date, timeZone: TimeZone = .current) {
        self.timeZone = timeZone
        super.init(from: from, to: to)
    }

    public init(at date: Date, spanMinutes: Int, timeZone: TimeZone = .current) {
        self.timeZone = timeZone
        super.init(at: date, spanMinutes: spanMinutes)
    }

    public convenience init(dateRange: DateRange, timeZone: TimeZone = .current) {
        self.init(from: dateRange.from, to: dateRange.to, timeZone: timeZone)
    }

    public func resetTimeZone(timeZone: TimeZone) -> TimeZoneDateRange {
        guard timeZone != self.timeZone else { return self }
        let dateFrom = from.dateByRemovingTimeZoneOffset(self.timeZone).dateByAddingTimeZoneOffset(timeZone)
        let dateTo = to.dateByRemovingTimeZoneOffset(self.timeZone).dateByAddingTimeZoneOffset(timeZone)
        return TimeZoneDateRange(from: dateFrom, to: dateTo, timeZone: timeZone)
    }
}

extension DateRange: Equatable { }
public func == (lhs: DateRange, rhs: DateRange) -> Bool {
    lhs.from == rhs.from && lhs.to == rhs.to
}

public func == (lhs: TimeZoneDateRange, rhs: TimeZoneDateRange) -> Bool {
    lhs.from == rhs.from && lhs.to == rhs.to && lhs.timeZone == rhs.timeZone
}

public extension DateRange {
    func toTimeRange(timeZone: TimeZone) -> TimeRange {
        TimeRange(from: from.toTime(timeZone: timeZone), to: to.toTime(timeZone: timeZone))
    }

    func openingRangeTo() -> DateRange {
        DateRange(from: from, to: to.add(seconds: -1))
    }
}

extension DateRange: CustomDebugStringConvertible {
    public var debugDescription: String {
        return "\(type(of: self))(from=\(from), to=\(to))"
    }
}
