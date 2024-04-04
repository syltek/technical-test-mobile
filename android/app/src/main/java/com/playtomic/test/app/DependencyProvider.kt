package com.playtomic.test.app

import androidx.fragment.app.Fragment
import com.anemonesdk.general.client.IHttpClient
import com.anemonesdk.general.client.OkHttpClient
import com.anemonesdk.service.ITenantService
import com.anemonesdk.service.TenantService
import com.playtomic.foundation.model.IContextProvider
import com.playtomic.mozart.managers.location.ILocationManager
import com.playtomic.mozart.managers.location.LocationManager
import com.playtomic.test.app.tenant.TenantListFragment
import com.playtomic.test.app.tenant.TenantListPresenter
import com.playtomic.test.app.tenant.TenantListViewState

class DependencyProvider(private val contextProvider: IContextProvider) {
    val httpClient: IHttpClient by lazy {
        OkHttpClient(baseUrl = "https://api.playtomic.io")
    }

    val fragments: Fragments by lazy {
        Fragments(services = services, managers = managers)
    }

    val managers: Managers by lazy {
        Managers(contextProvider)
    }

    val services: Services by lazy {
        Services(httpClient = httpClient)
    }

    class Fragments(val services: Services, val managers: Managers) {
        val rootFragment: Fragment
            get() = TenantListFragment().apply {
                presenter = TenantListPresenter(initialState = TenantListViewState(text = "Hello world"))
            }
    }

    class Managers(private val contextProvider: IContextProvider) {
        val locationManager: ILocationManager
            get() = LocationManager(contextProvider)
    }

    class Services(val httpClient: IHttpClient) {
        val tenantService: ITenantService
            get() = TenantService(httpClient = httpClient)
    }
}
