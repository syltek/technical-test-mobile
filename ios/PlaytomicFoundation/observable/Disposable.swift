//
//  Disposable.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 19/03/2019.
//  Copyright © 2019 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public final class Disposable {
    public let dispose: () -> Void

    init(_ dispose: @escaping () -> Void) {
        self.dispose = dispose
    }

    public func add(to disposal: IDisposal) {
        disposal.add(disposable: self)
    }
}

public final class Disposal: IDisposal {
    private var disposables: [Disposable]

    public init() {
        self.disposables = []
    }

    public func add(disposable: Disposable) {
        synchronized(self) {
            disposables.append(disposable)
        }
    }

    public func dispose() {
        synchronized(self) {
            disposables.forEach { $0.dispose() }
            disposables.removeAll()
        }
    }

    public func getDisposableSize() -> Int {
        disposables.count
    }
}

public protocol IDisposal {
    func add(disposable: Disposable)
    func dispose()
    func getDisposableSize() -> Int
}
