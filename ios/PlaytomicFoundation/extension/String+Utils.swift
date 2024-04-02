//
//  String+Utils.swift
//  My Sports
//
//  Created by Angel Luis Garcia on 12/04/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public extension String {
    var first: String {
        String(prefix(1))
    }

    var last: String {
        String(suffix(1))
    }

    var nonEmpty: String? {
        isEmpty ? nil : self
    }

    func before(first delimiter: Character) -> String {
        if let index = firstIndex(of: delimiter) {
            let before = prefix(upTo: index)
            return String(before)
        }
        return ""
    }

    func after(first delimiter: Character) -> String {
        if let index = firstIndex(of: delimiter) {
            let after = suffix(from: index).dropFirst()
            return String(after)
        }
        return ""
    }

    func before(first delimiter: String, includeDelimeter: Bool = false) -> String {
        if let range = range(of: delimiter) {
            let max = includeDelimeter ? index(range.lowerBound, offsetBy: delimiter.count) : range.lowerBound
            let firstPart = self[startIndex ..< max]
            return String(firstPart)
        }
        return ""
    }

    func after(first delimiter: String, includeDelimeter: Bool = false) -> String {
        if let range = range(of: delimiter) {
            let min = includeDelimeter ? range.lowerBound : index(range.lowerBound, offsetBy: delimiter.count)
            let firstPart = self[min ..< endIndex]
            return String(firstPart)
        }
        return ""
    }

    func uppercasedFirst() -> String {
        first.uppercased() + String(dropFirst())
    }

    func index(from: Int) -> Index {
        index(startIndex, offsetBy: from)
    }

    func substring(from: Int) -> String {
        guard from < count else { return "" }
        return String(self[index(from: from)...])
    }

    func substring(to: Int) -> String {
        String(self[..<index(from: min(count, to))])
    }

    func substring(with range: Range<Int>) -> String {
        let startIndex = index(from: range.lowerBound)
        let endIndex = index(from: range.upperBound)
        return String(self[startIndex ..< endIndex])
    }

    func toBool() -> Bool {
        NSString(string: self).boolValue
    }

    func replaceFirst(_ oldValue: String, _ newValue: String) -> String {
        range(of: oldValue).let { replacingCharacters(in: $0, with: newValue) } ?? self
    }

    func wordWrapping(size: Int) -> String {
        guard count > size else { return self }
        let wrap = substring(to: max(0, size))
        var parts = wrap.split(separator: " ").removingLast()
        if parts.isEmpty, let firstWord = split(separator: " ").first {
            parts += [firstWord]
        }
        return parts.joined(separator: " ")
    }

    func contains(_ other: String, ignoreCase: Bool, ignoreDiacritic: Bool) -> Bool {
        var options: NSString.CompareOptions = []
        if ignoreCase {
            options = [options, .caseInsensitive]
        }
        if ignoreDiacritic {
            options = [options, .diacriticInsensitive]
        }
        return range(of: other, options: options) != nil
    }

    func containsWord(starting: String) -> Bool {
        " \(self)".contains(" \(starting)", ignoreCase: true, ignoreDiacritic: true)
    }

    func split(usingRegex pattern: String) -> [String] {
        guard let regex = try? NSRegularExpression(pattern: pattern) else { return [] }
        let matches = regex.matches(in: self, range: NSRange(0 ..< utf16.count))
        let ranges = [startIndex ..< startIndex] + matches.compactMap { Range($0.range, in: self) } + [endIndex ..< endIndex]
        return (0 ... matches.count).map { String(self[ranges[$0].upperBound ..< ranges[$0 + 1].lowerBound]) }
    }

    func wordAt(index: String.Index) -> String? {
        guard index >= startIndex && index <= endIndex else { return nil }
        guard index == endIndex || !self[index].isWordSeparator else { return nil }
        var startIndex = index
        while startIndex > self.startIndex && !self[self.index(before: startIndex)].isWordSeparator {
            startIndex = self.index(before: startIndex)
        }
        var endIndex = index
        while endIndex < self.endIndex && !self[endIndex].isWordSeparator {
            endIndex = self.index(after: endIndex)
        }
        return String(self[startIndex ..< endIndex])
    }

    func trimWhitespaces() -> String {
        trimmingCharacters(in: .whitespaces)
    }

    var isBlank: Bool {
        trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    func slice(from: String, to: String) -> String? {
        (range(of: from)?.upperBound).flatMap { substringFrom in
            (range(of: to, range: substringFrom ..< endIndex)?.lowerBound).map { substringTo in
                String(self[substringFrom ..< substringTo])
            }
        }
    }
}

public extension String? {
    @inline(__always)
    func isNullOrEmpty() -> Bool {
        self == nil || self?.isEmpty == true
    }

    @inline(__always)
    func isNullOrBlank() -> Bool {
        self == nil || self?.isBlank == true
    }

    @inline(__always)
    func orEmpty() -> String {
        self ?? ""
    }
}

private extension Character {
    var isWordSeparator: Bool {
        isNewline || isWhitespace
    }
}
