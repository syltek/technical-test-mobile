package com.playtomic.mozart.managers.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.playtomic.foundation.extension.nonEmpty
import com.playtomic.foundation.extension.tryOrNull
import com.playtomic.foundation.model.Coordinate
import com.playtomic.foundation.model.FoundationException
import com.playtomic.foundation.model.IContextProvider
import com.playtomic.foundation.promise.Promise
import com.playtomic.mozart.managers.location.model.Location
import com.playtomic.mozart.managers.location.model.LocationServiceRequest
import com.playtomic.mozart.managers.location.model.LocationServiceStatus
import com.playtomicui.utils.PermissionActivity
import com.playtomicui.utils.PermissionActivity.Companion.canRequestPermission
import org.json.JSONArray
import java.util.*

/**
 * Created by agarcia on 23/12/2016.
 */

class LocationManager(private val contextProvider: IContextProvider, private val locale: Locale = Locale.getDefault()) :
    ILocationManager, LocationListener {

    companion object Defaults {
        private const val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        private const val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        const val minAccuracy = 500.0
        const val maxAge = 180.0
        const val timeout = 15.0
    }

    private val locationApiClient: FusedLocationProviderClient

    private val requests: MutableList<LocationServiceRequest>

    init {
        this.requests = ArrayList<LocationServiceRequest>()
        this.locationApiClient = LocationServices.getFusedLocationProviderClient(contextProvider.applicationContext)
        refreshLastLocation()
    }

    override var lastLocation: Location? = null

    override val locationStatus: LocationServiceStatus
        get() {
            val currentActivity = contextProvider.currentActivity
                ?: return LocationServiceStatus.notDetermined
            if (!isLocationEnabled) {
                return LocationServiceStatus.disabled
            }
            if (PermissionActivity.hasPermission(currentActivity, fineLocationPermission) || PermissionActivity.hasPermission(currentActivity, coarseLocationPermission)) {
                return LocationServiceStatus.authorized
            }
            if (!PermissionActivity.canRequestPermission(currentActivity, fineLocationPermission)) {
                return LocationServiceStatus.denied
            }
            return LocationServiceStatus.notDetermined
        }

    override val defaultCoordinate: Coordinate by lazy {
        val countryCode = locale.country.nonEmpty ?: "ES"
        try {
            // File origin: http://techslides.com/demos/country-capitals.json
            val inputStream = contextProvider.applicationContext.assets.open("country-capitals.json")
            val data: ByteArray = inputStream.readBytes()
            inputStream.close()
            val jsonArray = JSONArray(String(data))
            val jsonCountry = (0 until jsonArray.length()).toList().firstOrNull {
                jsonArray.getJSONObject(it).optString("CountryCode").uppercase() == countryCode
            }?.let { jsonArray.getJSONObject(it) }
            val lat = jsonCountry?.optString("CapitalLatitude")?.toDouble()
            val lon = jsonCountry?.optString("CapitalLongitude")?.toDouble()
            Coordinate(lat = lat!!, lon = lon!!)
        } catch (t: Exception) {
            assert(false) { "Country capitals for $countryCode not found or not properly parsed" }
            Coordinate(lat = 40.4168361, lon = -3.7039706)
        }
    }

    override fun findLocation(permissionRequest: LocationPermissionRequest, minAccuracy: Double, maxAge: Double, timeout: Double): Promise<Location> =
        Promise { fulfill, reject ->
            val request = LocationServiceRequest(minAccuracy, maxAge, timeout, fulfill, reject)
            addRequest(request, permissionRequest)
        }

    override fun hasPermission(): Boolean =
        locationStatus == LocationServiceStatus.authorized

    override fun requestLocationPermission() {
        val currentActivity = contextProvider.currentActivity ?: return
        if (!hasPermission() && canRequestPermission(currentActivity, fineLocationPermission)) {
            PermissionActivity.requestPermissions(currentActivity, arrayOf(fineLocationPermission)) { onPermissionResult() }
        }
    }

    // ****** INTERNAL *** /
    @SuppressLint("MissingPermission")
    private fun refreshLastLocation() {
        if (hasPermission()) {
            tryOrNull {
                locationApiClient.lastLocation.addOnSuccessListener {
                    it?.let { this.lastLocation = Location(it) }
                }
            }
        }
    }

    private fun addRequest(request: LocationServiceRequest, permissionRequest: LocationPermissionRequest) {
        val status = locationStatus

        // If location is rejected, then return error
        if (status === LocationServiceStatus.denied || status === LocationServiceStatus.disabled) {
            request.reject(FoundationException.denied)
            return
        }

        // If last location is already valid then early return
        val lastLocation = lastLocation
        if (lastLocation != null && request.isLocationValid(lastLocation)) {
            request.fulfill(lastLocation)
            return
        }

        // If no locationPermission given yet, then just request it
        if (status === LocationServiceStatus.notDetermined) {
            if (permissionRequest == LocationPermissionRequest.never) {
                request.reject(FoundationException.denied)
                return
            }
            requestLocationPermission()

            scheduleRequestTimeout(request)
            synchronized(requests) {
                requests.add(request)
            }
            return
        }

        scheduleRequestTimeout(request)

        try {
            startLocationUpdates(request)
            synchronized(requests) {
                requests.add(request)
            }
        } catch (ex: Exception) {
            request.reject(FoundationException.denied)
        }
    }

    private fun scheduleRequestTimeout(request: LocationServiceRequest) {

        // If timeout, set timer to be able to fail request after timeout
        if (request.timeout > 0) {
            request.timer = object : TimerTask() {
                override fun run() {
                    requestTimeOut(request)
                }
            }
            Timer().schedule(request.timer, (request.timeout * 1000).toLong())
        }
    }

    private val isLocationEnabled: Boolean
        get() {
            val locationManager = contextProvider.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    private fun requestTimeOut(request: LocationServiceRequest) {
        synchronized(requests) {
            if (requests.contains(request)) {
                requests.remove(request)
                request.reject(FoundationException.timeout)
            }
            if (requests.isEmpty()) {
                stopLocationUpdates()
            }
        }
    }

    private fun rejectPendingRequests(error: Exception) {
        synchronized(requests) {
            val requests = ArrayList(this.requests)
            this.requests.removeAll(requests)
            for (request in requests) {
                request.reject(error)
            }
            stopLocationUpdates()
        }
    }

    @Throws(SecurityException::class)
    private fun startLocationUpdates(request: LocationServiceRequest) {
        locationApiClient.requestLocationUpdates(request.locationRequest, this, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        locationApiClient.removeLocationUpdates(this)
    }

    /*********************************************
     * PermissionCallback
     */
    private fun onPermissionResult() {
        refreshLastLocation()
        synchronized(requests) {
            if (!requests.isEmpty()) {
                try {
                    startLocationUpdates(requests[0])
                } catch (ex: Exception) {
                    rejectPendingRequests(ex)
                }
            }
        }
    }

    /*********************************************
     * LocationListener
     */
    override fun onLocationChanged(loc: android.location.Location) {
        lastLocation = Location(loc)
        synchronized(requests) {
            val location = lastLocation ?: return
            val validRequests = requests.filter { it.isLocationValid(location) }

            for (request in validRequests) {
                requests.remove(request)
                request.timer?.cancel()
                request.fulfill(location)
            }

            if (requests.isEmpty()) {
                stopLocationUpdates()
            }
        }
    }
}
