//
//  Result.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 19/03/2019.
//  Copyright © 2019 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public enum Result<T> {
    case success(T)
    case failure(Error)

    @discardableResult
    public func onSuccess(action: (T) -> Void) -> Result<T> {
        if case let .success(value) = self {
            action(value)
        }
        return self
    }

    @discardableResult
    public func onFailure(action: (Error) -> Void) -> Result<T> {
        if case let .failure(error) = self {
            action(error)
        }
        return self
    }
}
