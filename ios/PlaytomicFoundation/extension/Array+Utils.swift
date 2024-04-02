//
//  Array+Utils.swift
//  My Sports
//
//  Created by Angel Luis Garcia on 14/07/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import CoreAudio
import Foundation

public extension Collection where Element: Hashable {
    func containsAll(elements: [Element]) -> Bool {
        Set(elements).isSubset(of: Set(self))
    }
}

public extension Array where Element: Hashable {
    // This method does not guarantee ordering. Use unique if order required
    func distinct() -> [Element] {
        Array(Set(self))
    }

    func unique() -> [Iterator.Element] {
        var seen: Set<Iterator.Element> = []
        return filter { seen.insert($0).inserted }
    }

    func removing(elements: [Element]) -> [Element] {
        filter { !elements.contains($0) }
    }

    // Returns items existing in both collections. Preserve the order of the orignal one
    func intersect(other: [Element]) -> [Element] {
        filter { other.contains($0) }
    }

    func toIndexMap() -> [Element: Int] {
        var map = [Element: Int]()
        for index in 0 ..< count {
            map[self[index]] = index
        }
        return map
    }
}

public extension Array where Element: Equatable {
    mutating func remove(element: Element) {
        guard let index = firstIndex(of: element) else { return }
        remove(at: index)
    }

    func removing(element: Element) -> [Element] {
        if let index = firstIndex(of: element) {
            var newArray = self
            newArray.remove(at: index)
            return newArray
        }
        return self
    }

    func removingAll(element: Element) -> [Element] {
        var newArray = self
        newArray.removeAll(where: { $0 == element })
        return newArray
    }

    func removingDuplicates() -> [Element] {
        var result = [Element]()
        forEach { value in
            if !result.contains(value) {
                result.append(value)
            }
        }
        return result
    }

    func removing(at index: Int) -> [Element] {
        var newArray = self
        newArray.remove(at: index)
        return newArray
    }
}

public extension Array where Element: Collection {
    func flatten() -> [Element.Element] {
        flatMap { $0 }
    }
}

public extension Collection {
    func sortedBy(selector: (Element) -> some Comparable) -> [Element] {
        sorted { selector($0) < selector($1) }
    }

    func sortedBy(_ selector1: (Element) -> some Comparable, _ selector2: (Element) -> some Comparable) -> [Element] {
        sorted {
            let val0 = selector1($0)
            let val1 = selector1($1)
            if val0 == val1 {
                return selector2($0) < selector2($1)
            } else {
                return val0 < val1
            }
        }
    }

    func sortedBy(
        _ selector1: (Element) -> some Comparable,
        _ selector2: (Element) -> some Comparable,
        _ selector3: (Element) -> some Comparable
    ) -> [Element] {
        sorted {
            let val0 = selector1($0)
            let val1 = selector1($1)
            if val0 == val1 {
                let val20 = selector2($0)
                let val21 = selector2($1)
                if val20 == val21 {
                    return selector3($0) < selector3($1)
                } else {
                    return val20 < val21
                }
            } else {
                return val0 < val1
            }
        }
    }

    func associatedBy<K: Hashable, V>(_ keySelector: (Iterator.Element) -> K, _ valueTransformer: (Iterator.Element) -> V) -> [K: V] {
        var dict = [K: V]()
        for element in self {
            dict[keySelector(element)] = valueTransformer(element)
        }
        return dict
    }

    var isNotEmpty: Bool {
        !isEmpty
    }

    var nonEmpty: Self? {
        guard !isEmpty else { return nil }
        return self
    }
}

public extension RangeReplaceableCollection {
    mutating func dropFirst(where element: (Iterator.Element) -> Bool) -> Iterator.Element? {
        guard let index = firstIndex(where: element) else { return nil }

        return remove(at: index)
    }
}

public func compareBy<Element>(
    _ selector1: @escaping (Element) -> some Comparable,
    _ selector2: @escaping (Element) -> some Comparable,
    _ selector3: @escaping (Element) -> some Comparable
) -> ((Element, Element) -> Bool) {
    {
        let val0 = selector1($0)
        let val1 = selector1($1)
        if val0 == val1 {
            let val20 = selector2($0)
            let val21 = selector2($1)
            if val20 == val21 {
                return selector3($0) < selector3($1)
            } else {
                return val20 < val21
            }
        } else {
            return val0 < val1
        }
    }
}

public func compareBy<Element>(
    _ selector1: @escaping (Element) -> some Comparable,
    _ selector2: @escaping (Element) -> some Comparable,
    _ selector3: @escaping (Element) -> some Comparable,
    _ selector4: @escaping (Element) -> some Comparable
) -> ((Element, Element) -> Bool) {
    {
        let val10 = selector1($0)
        let val11 = selector1($1)
        if val10 == val11 {
            let val20 = selector2($0)
            let val21 = selector2($1)
            if val20 == val21 {
                let val30 = selector3($0)
                let val31 = selector3($1)
                if val30 == val31 {
                    return selector4($0) < selector4($1)
                } else {
                    return val30 < val31
                }
            } else {
                return val20 < val21
            }
        } else {
            return val10 < val11
        }
    }
}

public extension Array {
    func minBy(selector: (Element) -> some Comparable) -> Element? {
        guard count > 0 else { return nil }
        var minElem = self[0]
        var minValue = selector(minElem)
        for index in 1 ..< count {
            let elem = self[index]
            let value = selector(elem)
            if minValue > value {
                minElem = elem
                minValue = value
            }
        }
        return minElem
    }

    func removingLast() -> [Element] {
        removingSubrange(fromIndex: count - 1)
    }

    func removingSubrange(fromIndex: Int = 0, toIndex: Int = Int.max) -> [Element] {
        var newList = self
        let fromBoundedIndex = Swift.max(0, fromIndex)
        let toBoundedIndex = Swift.min(newList.count, toIndex)
        if fromBoundedIndex > toBoundedIndex { return newList }
        newList.removeSubrange(fromBoundedIndex ..< toBoundedIndex)
        return newList
    }

    func replacingSubrange(fromIndex: Int, toIndex: Int, with: [Element]) -> [Element] {
        var newList = self
        let fromBoundedIndex = Swift.max(0, fromIndex)
        let toBoundedIndex = Swift.min(newList.count, toIndex)
        newList.replaceSubrange(fromBoundedIndex ..< toBoundedIndex, with: with)
        return newList
    }

    func forEachIndexed(_ body: (Int, Element) -> Void) {
        for index in 0 ..< count {
            body(index, self[index])
        }
    }

    func mapIndexed<T>(_ transform: (Int, Element) throws -> T) rethrows -> [T] {
        var results = [T]()
        for index in 0 ..< count {
            try results.append(transform(index, self[index]))
        }
        return results
    }

    func chunked(size: Int) -> [[Element]] {
        stride(from: 0, to: count, by: size).map {
            Array(self[$0 ..< Swift.min($0 + size, count)])
        }
    }

    func groupingBy<Key: Hashable>(selector: (Element) -> Key) -> [Key: [Element]] {
        groupingByKeys { [selector($0)] }
    }

    func groupingByKeys<Key: Hashable>(selector: (Element) -> [Key]) -> [Key: [Element]] {
        var dictionary: [Key: [Element]] = [:]
        forEach { element in
            selector(element).forEach { key in
                var elements = dictionary[key] ?? []
                elements.append(element)
                dictionary[key] = elements
            }
        }
        return dictionary
    }

    func groupByDistinct<Key: Hashable>(selector: (Element) -> Key?) -> [Key: Element] {
        var dictionary: [Key: Element] = [:]
        forEach { element in
            if let key = selector(element) {
                dictionary[key] = element
            }
        }
        return dictionary
    }

    func getOrNull(index: Int) -> Element? {
        guard index >= 0, index < count else { return nil }
        return self[index]
    }

    func removingFirst(_ predicate: (Element) throws -> Bool) -> [Element] {
        var newList = self
        if let index = try? newList.firstIndex(where: predicate) {
            newList.remove(at: index)
        }
        return newList
    }

    func appending(_ element: Element) -> Self {
        var copySelf = self
        copySelf.append(element)

        return copySelf
    }

    func firstMap<T>(_ transform: (Element) -> T?) -> T? {
        for element in self {
            if let newValue = transform(element) {
                return newValue
            }
        }
        return nil
    }

    var nilIfEmpty: Array? {
        guard isNotEmpty else { return nil }
        return self
    }
}
