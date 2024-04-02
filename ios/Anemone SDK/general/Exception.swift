//
//  Exception.swift
//  Anemone SDK
//
//  Created by Manuel Gonzalez Villegas on 5/12/16.
//  Copyright © 2016 Syltek Solutions S.L. All rights reserved.
//
// swiftlint:disable identifier_name
import Foundation

public enum AnemoneException: Error {
    case notFound
    case jsonInvalidFormat(key: String?)
    case notMappable
    case timeout
    case denied
    case wrongUrl
    case network(response: HttpResponse, error: Error?, serverMessage: Message?)
    case configurationMissing(error: String)
    case generic(error: String?)
    case database(error: Error)
}

extension AnemoneException: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case let AnemoneException.network(_, error, message):
            return message?.message ?? (error as? LocalizedError)?.errorDescription
        default:
            return nil
        }
    }
}

public extension AnemoneException {
    var isConnectivityError: Bool {
        switch self {
        case .timeout:
            return true
        case let .network(response, _, _):
            return response.code <= 0
        default:
            return false
        }
    }

    var isPrivateTenantError: Bool {
        switch self {
        case let .network(_, _, serverMessage):
            return serverMessage?.status == "BOOKING_PRIVATE"
        default:
            return false
        }
    }

    var isForbiddenError: Bool {
        switch self {
        case let .network(response, _, _):
            return response.code == 403
        default:
            return false
        }
    }

    var isNotFoundError: Bool {
        switch self {
        case let .network(response, _, _): return response.code == 404
        case .notFound: return true
        default: return false
        }
    }
}

extension AnemoneException: Equatable { }

public func == (lhs: AnemoneException, rhs: AnemoneException) -> Bool {
    switch (lhs, rhs) {
    case (.notFound, .notFound):
        return true
    case let (.jsonInvalidFormat(r1), .jsonInvalidFormat(r2)) where r1 == r2:
        return true
    case (.notMappable, .notMappable):
        return true
    case (.timeout, .timeout):
        return true
    case (.denied, .denied):
        return true
    case (.wrongUrl, .wrongUrl):
        return true
    case let (.network(r1, _, _), .network(r2, _, _)) where r1 == r2:
        return true
    case let (.configurationMissing(r1), .configurationMissing(r2)) where r1 == r2:
        return true
    case let (.generic(r1), .generic(r2)) where r1 == r2:
        return true
    default:
        return false
    }
}
