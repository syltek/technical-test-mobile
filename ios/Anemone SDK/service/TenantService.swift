//
//  TenantService.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 14/02/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

final class TenantService: ITenantService {
    private let httpClient: IHttpClient

    init(httpClient: IHttpClient) {
        self.httpClient = httpClient
    }

    func search(name: String, pagination: PaginationOptions?) -> Promise<[Tenant]> {
        let endpoint = "/v1/tenants"
        var params: [String: Any] = [
            "tenant_name": name,
            "playtomic_status": "ACTIVE"
        ]
        if let pagination = pagination {
            params += pagination.params
        }
        return httpClient.get(endpoint: endpoint, params: params).then { JSONTransformer().map($0) }
    }

    func search(coordinate: Coordinate, radius: Int, sportId: SportId?, pagination: PaginationOptions?) -> Promise<[Tenant]> {
        let endpoint = "/v1/tenants"
        var params: [String: Any] = [
            "coordinate": coordinate,
            "radius": radius,
            "playtomic_status": "ACTIVE"
        ]
        if let sportId = sportId {
            params["sport_id"] = sportId
        }
        if let pagination = pagination {
            params += pagination.params
        }
        return httpClient.get(endpoint: endpoint, params: params).then { JSONTransformer().map($0) }
    }

    func search(tenantIds: [TenantId]) -> Promise<[Tenant]> {
        guard !tenantIds.isEmpty else { return Promise(value: []) }
        let endpoint = "/v1/tenants"
        let params: [String: Any] = [
            "tenant_id": tenantIds,
            "playtomic_status": "ACTIVE"
        ]
        return httpClient.get(endpoint: endpoint, params: params)
            .then { JSONTransformer().map($0) }
            .then(map: { (tenants: [Tenant]) -> [Tenant] in
                tenantIds.compactMap { tenantId in tenants.first { $0.id == tenantId } }
            })
    }

    func search(googlePlaceId: String) -> Promise<[Tenant]> {
        let params: [String: Any] = [
            "google_place_id": googlePlaceId
        ]
        return httpClient.get(endpoint: "/v1/tenants", params: params).then { JSONTransformer().map($0) }
    }

    func fetchDetail(id: TenantId) -> Promise<Tenant> {
        let endpoint = "/v1/tenants/\(id)"
        return httpClient.get(endpoint: endpoint, params: nil).then { JSONTransformer().map($0) }
    }

    func fetchDetails(tenantIds: [TenantId]) -> Promise<[Tenant]> {
        guard !tenantIds.isEmpty else { return Promise(value: []) }
        return httpClient.get(endpoint: "/v1/tenants", params: ["tenant_id": tenantIds]).then { JSONTransformer().map($0) }
    }
}
