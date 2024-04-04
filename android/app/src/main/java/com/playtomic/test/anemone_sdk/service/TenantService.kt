package com.anemonesdk.service

import com.anemonesdk.general.client.IHttpClient
import com.anemonesdk.general.json.JSONTransformer
import com.anemonesdk.model.config.SportId
import com.anemonesdk.model.tenant.Tenant
import com.anemonesdk.model.tenant.TenantId
import com.anemonesdk.service.request.PaginationOptions
import com.playtomic.foundation.extension.compactMap
import com.playtomic.foundation.model.Coordinate
import com.playtomic.foundation.promise.Promise

/**
 * Created by agarcia on 15/02/2017.
 */

internal class TenantService(private val httpClient: IHttpClient) : ITenantService {

    override fun search(name: String, pagination: PaginationOptions?): Promise<List<Tenant>> {
        val endpoint = "/v1/tenants"
        val params = mutableMapOf<String, Any>(
            "tenant_name" to name,
            "playtomic_status" to "ACTIVE"
        )
        if (pagination != null) {
            params += pagination.params
        }
        return httpClient.get(endpoint = endpoint, params = params)
            .then(JSONTransformer(Tenant::class.java)::mapArray)
    }

    override fun search(coordinate: Coordinate, radius: Int, sportId: SportId?, pagination: PaginationOptions?): Promise<List<Tenant>> {
        val endpoint = "/v1/tenants"
        val params = mutableMapOf<String, Any>(
            "coordinate" to coordinate,
            "radius" to radius,
            "playtomic_status" to "ACTIVE"
        )
        if (sportId != null) {
            params["sport_id"] = sportId
        }
        if (pagination != null) {
            params += pagination.params
        }
        return httpClient.get(endpoint = endpoint, params = params)
            .then(JSONTransformer(Tenant::class.java)::mapArray)
    }

    override fun search(tenantIds: List<TenantId>): Promise<List<Tenant>> {
        if (tenantIds.isEmpty()) {
            return Promise(value = listOf())
        }
        val endpoint = "/v1/tenants"
        val params = mutableMapOf<String, Any>(
            "tenant_id" to tenantIds,
            "playtomic_status" to "ACTIVE"
        )
        return httpClient.get(endpoint = endpoint, params = params)
            .then(JSONTransformer(Tenant::class.java)::mapArray)
            .then(map = { tenants ->
                tenantIds.compactMap { tenantId -> tenants.firstOrNull { it.id == tenantId } }
            })
    }

    override fun search(googlePlaceId: String): Promise<List<Tenant>> {
        val params = mutableMapOf<String, Any>(
            "google_place_id" to googlePlaceId
        )
        return httpClient.get(endpoint = "/v1/tenants", params = params)
            .then(JSONTransformer(Tenant::class.java)::mapArray)
    }

    override fun fetchDetail(id: TenantId): Promise<Tenant> {
        val endpoint = "/v1/tenants/$id"
        return httpClient.get(endpoint = endpoint, params = null)
            .then(JSONTransformer(Tenant::class.java)::mapObject)
    }
}
