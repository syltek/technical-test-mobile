//
//  ITenantService.swift
//  Anemone SDK
//
//  Created by Angel Luis Garcia on 14/02/2017.
//  Copyright © 2017 Syltek Solutions S.L. All rights reserved.
//

import Foundation

public protocol ITenantService {
    func search(name: String, pagination: PaginationOptions?) -> Promise<[Tenant]>
    func search(coordinate: Coordinate, radius: Int, sportId: SportId?, pagination: PaginationOptions?) -> Promise<[Tenant]>
    func search(tenantIds: [TenantId]) -> Promise<[Tenant]>
    func search(googlePlaceId: String) -> Promise<[Tenant]>

    func fetchDetail(id: TenantId) -> Promise<Tenant>
}
