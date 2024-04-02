//
//  Executor.swift
//  PlaytomicFoundation
//
//  Created by Manuel González Villegas on 28/11/2019.
//  Copyright © 2019 Playtomic. All rights reserved.
//

import Foundation

public class Executor {
    public class CancellableTask {
        public enum Status {
            case waiting, completed, canceled
        }

        public private(set) var status: Status = .waiting
        var task: DispatchWorkItem!
        private let job: () -> Void

        init(job: @escaping () -> Void) {
            self.job = job
            self.task = DispatchWorkItem { self.run() }
        }

        public func run() {
            if status == .waiting {
                job()
                status = .completed
                task = nil
            }
        }

        public func cancel() {
            if status == .waiting {
                task.cancel()
                task = nil
                status = .canceled
            }
        }
    }

    @discardableResult
    public static func execute(after: TimeInterval = 0, inBackground: Bool, job: @escaping () -> Void) -> CancellableTask? {
        if after > 0 || inBackground || !Thread.isMainThread {
            let cancellableTask = CancellableTask(job: job)
            let queue = inBackground ? DispatchQueue.global(qos: .background) : DispatchQueue.main
            queue.asyncAfter(deadline: .now() + after, execute: cancellableTask.task)
            return cancellableTask
        } else {
            job()
            return nil
        }
    }
}
