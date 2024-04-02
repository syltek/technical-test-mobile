//
//  Decimal+Utils.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 16/02/2018.
//  Copyright © 2018 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public extension Decimal {
    var doubleValue: Double {
        (self as NSNumber).doubleValue
    }

    var intValue: Int {
        (doubleValue >= Double(Int.max)) ? Int.max : Int(doubleValue)
    }

    func roundedValue(decimals: Int = 2) -> Decimal {
        let power = pow(10, decimals)
        return Decimal(round(((self * power) as NSNumber).doubleValue)) / power
    }

    ///    Formats the ``Decimal`` with the specified locale, number of decimals, and option to keep trailing zeros.
    ///
    ///    - Returns: a formatted ``String`` representation of the ``Decimal``.
    ///    - Parameters:
    ///      - locale: The locale to be used for formatting. _If `null`, the default locale is used._
    ///      - decimals: The number of decimal places to round the value to _(default = 2)_.
    ///      - stripTrailingZero: `true` to strip trailing zeros, `false` to keep them _(default = true)_.
    func formattedDescription(decimals: Int = 2, stripTrailingZero: Bool = true, locale: Locale = Locale.current) -> String {
        var result = Decimal()
        var localCopy = abs(self)
        NSDecimalRound(&result, &localCopy, decimals, RoundingMode.down)
        let roundedAmount = result
        let fraction = modf((roundedAmount as NSNumber).doubleValue).1
        let formatter = NumberFormatter()
        formatter.locale = locale
        formatter.numberStyle = .decimal
        formatter.maximumFractionDigits = fraction > 0 ? decimals : 0
        var minDigits: Int {
            switch true {
            case stripTrailingZero: 0
            case !stripTrailingZero && fraction <= Double.zero: 1
            default: decimals
            }
        }
        formatter.minimumFractionDigits = minDigits
        return "\(isSignMinus ? "-" : "")\(formatter.string(from: NSDecimalNumber(decimal: roundedAmount)) ?? "-")"
    }

    func toString() -> String {
        let formatter = NumberFormatter()
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = isZero ? 0 : 1
        return formatter.string(from: self as NSDecimalNumber) ?? "0"
    }
}
