//
//  Date+Utils.swift
//  Anemone SDK
//
//  Created by Angel Garcia on 12/01/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//
// swiftlint:disable force_unwrapping

import Foundation

public extension Date {
    static let defaultFormat = "yyyy-MM-dd'T'HH:mm:ss"

    static func from(day: Date, time: Date, timeZone: TimeZone? = nil) -> Date {
        let calendar = NSCalendar.current
        let timeComponents = calendar.dateComponents(in: timeZone ?? .current, from: time)
        var dateComponents = calendar.dateComponents(in: timeZone ?? .current, from: day)

        dateComponents.setValue(timeComponents.hour, for: .hour)
        dateComponents.setValue(timeComponents.minute, for: .minute)
        dateComponents.setValue(timeComponents.second, for: .second)
        dateComponents.setValue(0, for: .nanosecond)

        let date: Date! = calendar.date(from: dateComponents)
        return date
    }

    static func from(string: String, format: String = Date.defaultFormat, timeZone: TimeZone = .current) -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = timeZone
        return dateFormatter.date(from: string)
    }

    static func today(timeZone: TimeZone? = nil) -> Date {
        Date().midnight(timeZone: timeZone)
    }

    static func tomorrow(timeZone: TimeZone? = nil) -> Date {
        today(timeZone: timeZone).add(days: 1, timeZone: timeZone)
    }

    static func yesterday(timeZone: TimeZone? = nil) -> Date {
        today(timeZone: timeZone).add(days: -1, timeZone: timeZone)
    }

    func subtract(days: Int, timeZone: TimeZone? = nil) -> Date {
        add(days: -days, timeZone: timeZone)
    }

    func midnight(timeZone: TimeZone? = nil, locale: Locale? = nil) -> Date {
        var calendar = NSCalendar.current
        calendar.locale = locale ?? .current
        calendar.timeZone = timeZone ?? .current
        return calendar.startOfDay(for: self)
    }

    func endOfDay(timeZone: TimeZone? = nil, locale: Locale? = nil) -> Date {
        var calendar = NSCalendar.current
        calendar.locale = locale ?? .current
        calendar.timeZone = timeZone ?? .current
        return calendar.date(byAdding: DateComponents(day: 1, second: -1), to: calendar.startOfDay(for: self))!
    }

    func startOfWeek(timeZone: TimeZone? = nil, locale: Locale? = nil) -> Date {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        calendar.locale = locale ?? .current
        return calendar.date(from: calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: self))!
    }

    func endOfWeek(timeZone: TimeZone? = nil, locale: Locale? = nil) -> Date {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        calendar.locale = locale ?? .current
        let startOfWeek = startOfWeek(timeZone: timeZone, locale: locale)
        return calendar.date(byAdding: DateComponents(day: 7, second: -1), to: startOfWeek)!
    }

    func startOfMonth(timeZone: TimeZone? = nil, locale: Locale? = nil) -> Date {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        calendar.locale = locale ?? .current
        return calendar.date(from: calendar.dateComponents([.year, .month], from: calendar.startOfDay(for: self)))!
    }

    func endOfMonth(timeZone: TimeZone? = nil, locale: Locale? = nil) -> Date {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        calendar.locale = locale ?? .current
        let startOfMonth = startOfMonth(timeZone: timeZone, locale: locale)
        return calendar.date(byAdding: DateComponents(month: 1, second: -1), to: startOfMonth)!
    }

    func add(years: Int) -> Date {
        let calendar = NSCalendar.current
        let date: Date! = calendar.date(byAdding: .year, value: years, to: self)
        return date
    }

    func add(hours: Int) -> Date {
        let calendar = NSCalendar.current
        let date: Date! = calendar.date(byAdding: .hour, value: hours, to: self)
        return date
    }

    func add(minutes: Int) -> Date {
        let calendar = NSCalendar.current
        let date: Date! = calendar.date(byAdding: .minute, value: minutes, to: self)
        return date
    }

    func add(seconds: Int) -> Date {
        let calendar = NSCalendar.current
        let date: Date! = calendar.date(byAdding: .second, value: seconds, to: self)
        return date
    }

    func add(days: Int, timeZone: TimeZone? = nil) -> Date {
        var calendar = NSCalendar.current
        calendar.timeZone = timeZone ?? .current
        let date: Date! = calendar.date(byAdding: .day, value: days, to: self)

        return date
    }

    func isMidnight(timeZone: TimeZone? = nil) -> Bool {
        var calendar = NSCalendar.current
        calendar.timeZone = timeZone ?? .current
        return calendar.startOfDay(for: self) == self
    }

    func isToday(timeZone: TimeZone? = nil) -> Bool {
        isSameDay(day: Date.today(timeZone: timeZone), timeZone: timeZone)
    }

    func isYesterday(timeZone: TimeZone? = nil) -> Bool {
        isSameDay(day: Date.yesterday(timeZone: timeZone), timeZone: timeZone)
    }

    func isTomorrow(timeZone: TimeZone? = nil) -> Bool {
        isSameDay(day: Date.tomorrow(timeZone: timeZone), timeZone: timeZone)
    }

    func isSameDay(day: Date, timeZone: TimeZone? = nil) -> Bool {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        let dayComponents = calendar.dateComponents([.day, .month, .year], from: day)
        let selfComponents = calendar.dateComponents([.day, .month, .year], from: self)
        return dayComponents == selfComponents
    }

    func isSameMonth(day: Date, timeZone: TimeZone? = nil) -> Bool {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        let dayComponents = calendar.dateComponents([.month, .year], from: day)
        let selfComponents = calendar.dateComponents([.month, .year], from: self)
        return dayComponents == selfComponents
    }

    func dateByAddingTimeZoneOffset(_ timeZone: TimeZone) -> Date {
        addingTimeInterval(-TimeInterval(timeZone.secondsFromGMT(for: self)))
    }

    func dateByRemovingTimeZoneOffset(_ timeZone: TimeZone) -> Date {
        addingTimeInterval(TimeInterval(timeZone.secondsFromGMT(for: self)))
    }

    func years(to: Date) -> Int {
        Calendar.current.dateComponents([.year], from: self, to: to).year ?? 0
    }

    func days(to: Date) -> Int {
        Calendar.current.dateComponents([.day], from: self, to: to).day ?? 0
    }

    func hours(to: Date) -> Int {
        Calendar.current.dateComponents([.hour], from: self, to: to).hour ?? 0
    }

    func minutes(to: Date) -> Int {
        Calendar.current.dateComponents([.minute], from: self, to: to).minute ?? 0
    }

    func seconds(to: Date) -> Int {
        Calendar.current.dateComponents([.second], from: self, to: to).second ?? 0
    }

    func isFuture() -> Bool {
        timeIntervalSinceNow > 0
    }

    func isPast() -> Bool {
        timeIntervalSinceNow < 0
    }

    func dayOfYear(timeZone: TimeZone? = nil) -> Int {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        return calendar.ordinality(of: .day, in: .year, for: self) ?? 0
    }

    func minutes(timeZone: TimeZone? = nil) -> Int {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        return calendar.component(.minute, from: self)
    }

    func hours(timeZone: TimeZone? = nil) -> Int {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        return calendar.component(.hour, from: self)
    }

    func seconds(timeZone: TimeZone? = nil) -> Int {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        return calendar.component(.second, from: self)
    }

    func toTime(timeZone: TimeZone) -> Time {
        Time.of(hour: hours(timeZone: timeZone), minute: minutes(timeZone: timeZone), second: seconds(timeZone: timeZone))
    }

    func toString(format: String, timeZone: TimeZone) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = timeZone
        return dateFormatter.string(from: self)
    }

    func isSameWeekDay(_ day: Date, timeZone: TimeZone? = nil) -> Bool {
        var calendar = Calendar.current
        calendar.timeZone = timeZone ?? .current
        let dayComponents = calendar.dateComponents([.weekday], from: day)
        let selfComponents = calendar.dateComponents([.weekday], from: self)
        return dayComponents == selfComponents
    }

    func daysBetween(_ toDate: Date) -> Int {
        Int(abs(timeIntervalSince(toDate) / 3600 / 24))
    }

    func weeksTo(_ toDate: Date) -> Int {
        Int(abs(timeIntervalSince(toDate) / 3600 / 24 / 7))
    }

    func formattedDateWithFormat(format: String, timeZone: TimeZone? = nil) -> String {
        let dayFormatter = DateFormatter()
        dayFormatter.locale = Locale(identifier: Locale.current.identifier)
        dayFormatter.dateFormat = format
        dayFormatter.timeZone = timeZone ?? .current
        return dayFormatter.string(from: self)
    }
}
