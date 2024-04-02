//
//  DependencyProvider.swift
//  Playtomic
//
//  Created by Angel Luis Garcia on 2/4/24.
//

import Foundation
import UIKit

class DependencyProvider {
    lazy var httpClient: IHttpClient = {
        HttpClient(baseUrl: "https://api.playtomic.io")
    }()

    lazy var viewControllers: ViewControllers = {
        ViewControllers(services: services, managers: managers)
    }()

    lazy var managers: Managers = {
        Managers()
    }()

    lazy var services: Services = {
        Services(httpClient: httpClient)
    }()

    struct ViewControllers {
        let services: Services
        let managers: Managers

        var rootViewController: UIViewController {
            TenantListViewController().apply { vc in
                vc.presenter = TenantListPresenter(initialState: TenantListViewState(text: "Hello world"))
            }
        }
    }

    struct Managers {
        var locationManager: ILocationManager {
            LocationManager()
        }
    }

    struct Services {
        let httpClient: IHttpClient

        var tenantService: ITenantService {
            TenantService(httpClient: httpClient)
        }
    }
}
