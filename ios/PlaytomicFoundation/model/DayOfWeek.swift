//
//  DayOfWeek.swift
//  PlaytomicFoundation
//
//  Created by Angel Luis Garcia on 29/04/2019.
//  Copyright © 2019 Playtomic. All rights reserved.
//

import Foundation

// swiftlint:disable force_unwrapping
private let gregorianCalendar = NSCalendar(identifier: .gregorian)!
// swiftlint:enable force_unwrapping

public enum DayOfWeek: String, Equatable {
    case monday = "MONDAY"
    case tuesday = "TUESDAY"
    case wednesday = "WEDNESDAY"
    case thursday = "THURSDAY"
    case friday = "FRIDAY"
    case saturday = "SATURDAY"
    case sunday = "SUNDAY"

    public var isWeekend: Bool {
        switch self {
        case .saturday, .sunday:
            return true
        default:
            return false
        }
    }

    public static var all: [DayOfWeek] = [.monday, .tuesday, .wednesday, .thursday, .friday, .saturday, .sunday]

    public func formattedDay(format: String = "EEEE", locale: Locale = Locale(identifier: Locale.current.identifier)) -> String {
        let today = Date()
        let calendar = NSCalendar.current
        let todayWeekDay = calendar.component(.weekday, from: today) - calendar.firstWeekday
        let dayOfWeekPos = calendarDayOfWeek() - calendar.firstWeekday
        let addDays = calendar.weekdaySymbols.count - todayWeekDay + dayOfWeekPos
        let date = today.add(days: addDays)

        let dateFormatter = DateFormatter()
        dateFormatter.locale = locale
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: date)
    }

    public func calendarDayOfWeek() -> Int {
        // Using position of Calendar.weekdaySymbols
        switch self {
        case .sunday: return 1
        case .monday: return 2
        case .tuesday: return 3
        case .wednesday: return 4
        case .thursday: return 5
        case .friday: return 6
        case .saturday: return 7
        }
    }
}

extension DayOfWeek: CustomStringConvertible {
    public var description: String {
        rawValue
    }
}

public extension Date {
    func dayOfWeek() -> DayOfWeek {
        let weekDay = gregorianCalendar.component(.weekday, from: self)
        switch weekDay {
        case 1: return .sunday
        case 2: return .monday
        case 3: return .tuesday
        case 4: return .wednesday
        case 5: return .thursday
        case 6: return .friday
        case 7: return .saturday
        default: fatalError("Unexpected calendar week day \(weekDay)")
        }
    }
}
