package com.anemonesdk.service

import com.anemonesdk.model.config.SportId
import com.anemonesdk.model.tenant.Tenant
import com.anemonesdk.model.tenant.TenantId
import com.anemonesdk.service.request.PaginationOptions
import com.playtomic.foundation.model.Coordinate
import com.playtomic.foundation.promise.Promise

/**
 * Created by agarcia on 15/02/2017.
 */

interface ITenantService {

    fun search(name: String, pagination: PaginationOptions?): Promise<List<Tenant>>
    fun search(coordinate: Coordinate, radius: Int, sportId: SportId?, pagination: PaginationOptions?): Promise<List<Tenant>>
    fun search(tenantIds: List<TenantId>): Promise<List<Tenant>>
    fun search(googlePlaceId: String): Promise<List<Tenant>>

    fun fetchDetail(id: TenantId): Promise<Tenant>
}
