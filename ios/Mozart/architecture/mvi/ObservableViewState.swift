//
//  ObservableViewState.swift
//  Mozart
//
//  Created by Angel Luis Garcia on 19/11/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//

import Foundation
import SwiftUI

public class ObservableViewState<S: ViewState>: ObservableObject {
    @Published public var value: S

    public init(value: S) {
        self.value = value
    }
}

private class ObservableViewStateWrapper<S: ViewState>: ObservableViewState<S> {
    let disposal = Disposal()

    init(observable: Observable<S>) {
        if let value = observable.value {
            super.init(value: value)
        } else {
            fatalError("Found nil in ObservableViewStateWrapper while unwrapping value: \(observable)")
        }
        observable.observeNext { [weak self] value in
            if let value {
                self?.value = value
            }
        }.add(to: disposal)
    }
}

public extension Observable where T: ViewState {
    func toObservableViewState() -> ObservableViewState<T> {
        ObservableViewStateWrapper(observable: self)
    }
}
