//
//  DataFile.swift
//  Anemone SDK
//
//  Created by Manuel González Villegas on 28/5/21.
//  Copyright © 2021 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public enum DataFile {
    case jpegPicture(fileName: String, fileData: Data)

    var contentType: String {
        switch self {
        case .jpegPicture:
            return "image/jpeg"
        }
    }

    var extensionFileName: String {
        switch self {
        case .jpegPicture:
            return ".jpeg"
        }
    }

    var name: String {
        switch self {
        case let .jpegPicture(fileName, _):
            return fileName
        }
    }

    var data: Data {
        switch self {
        case let .jpegPicture(_, data):
            return data
        }
    }
}

public struct MultipartDataFile {
    let name: String
    let dataFile: DataFile

    var contentType: String {
        dataFile.contentType
    }

    var extensionFileName: String {
        dataFile.extensionFileName
    }

    var fileName: String {
        dataFile.name
    }

    var data: Data {
        dataFile.data
    }
}
