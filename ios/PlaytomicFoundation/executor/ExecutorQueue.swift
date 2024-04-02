//
//  ExecutorQueue.swift
//  PlaytomicFoundation
//
//  Created by Angel Luis Garcia on 04/09/2020.
//  Copyright © 2020 Playtomic. All rights reserved.
//

import Foundation

public class ExecutorQueue {
    public var taskCount: Int { pendingTasks.count }
    public var isRunning: Observable<Bool> { _isRunning }
    public var isEmpty: Bool { pendingTasks.isEmpty }
    private let _isRunning = MutableObservable<Bool>(value: false)
    private var pendingTasks: [Task] = []

    public init() { }

    @discardableResult
    public func add<T>(_ promiseProvider: @escaping (() -> Promise<T>)) -> Promise<T> {
        let promiseTask = PromiseTask(promiseProvider: promiseProvider)
        add(task: promiseTask)
        return promiseTask.pendingPromise
    }

    private func add(task: Task) {
        pendingTasks.append(task)
        if isRunning.value == false {
            processNext()
        }
    }

    private func processNext() {
        if !pendingTasks.isEmpty {
            _isRunning.distinctValue = true
            pendingTasks.removeFirst().execute().always(processNext)
        } else {
            _isRunning.distinctValue = false
        }
    }
}

private protocol Task {
    func execute() -> Promise<Void>
}

private class PromiseTask<T>: Task {
    let promiseProvider: () -> Promise<T>
    let pendingPromise: PendingPromise<T>

    init(promiseProvider: @escaping (() -> Promise<T>)) {
        self.promiseProvider = promiseProvider
        self.pendingPromise = PendingPromise()
    }

    func execute() -> Promise<Void> {
        promiseProvider()
            .then(pendingPromise.fulfill)
            .catchError(pendingPromise.reject)
            .then(map: { _ in () })
            .fulfillOnError(value: ())
    }
}
