//
//  FileStorage.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 08/02/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public class FileStorage: IFileStorage {
    public enum Exception: Error {
        case notFound, wrongUrl
    }

    let path: String

    public init(directory: FileManager.SearchPathDirectory) {
        let paths = NSSearchPathForDirectoriesInDomains(directory, .userDomainMask, true)
        self.path = paths[0]
    }

    public func writeData(_ data: Data, file: String) throws {
        let url = try fileURL(name: file)
        try data.write(to: url, options: Data.WritingOptions.atomic)
    }

    public func writeDataAsync(_ data: Data, file: String) -> Promise<Void> {
        Promise(executeInBackground: true) { fulfill, _ in
            try self.writeData(data, file: file)
            fulfill(())
        }
    }

    public func readData(file: String) -> Data? {
        guard let url = try? fileURL(name: file) else {
            return nil
        }
        return try? Data(contentsOf: url)
    }

    public func readDataAsync(file: String) -> Promise<Data> {
        Promise(executeInBackground: true) { fulfill, reject in
            if let data = self.readData(file: file) {
                fulfill(data)
            } else {
                reject(Exception.notFound)
            }
        }
    }

    public func removeData(file: String) throws {
        let url = try fileURL(name: file)
        try FileManager.default.removeItem(at: url)
    }

    public func removeDataAsync(file: String) -> Promise<Void> {
        Promise(executeInBackground: true) { fulfill, _ in
            try self.removeData(file: file)
            fulfill(())
        }
    }

    private func fileURL(name: String) throws -> URL {
        guard let url = URL(string: "file://\(path)/\(name)") else {
            throw Exception.wrongUrl
        }
        return url
    }
}
