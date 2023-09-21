package org.lightquark.maptracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.lightquark.maptracker.data.LocationRepository
import java.util.concurrent.Executors

/**
 * Allows Fragment to observer {@link Location} database, follow the state of location updates,
 * and start/stop receiving location updates.
 */
class LocationUpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )

    val receivingLocationUpdates: LiveData<Boolean> = locationRepository.receivingLocationUpdates

    val locationListLiveData = locationRepository.getLocations()

    fun startLocationUpdates() = locationRepository.startLocationUpdates()

    fun stopLocationUpdates() = locationRepository.stopLocationUpdates()

    fun cleanupDatabase() = locationRepository.cleanupDatabase()
}
