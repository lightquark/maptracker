package org.lightquark.maptracker.data

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import org.lightquark.maptracker.data.db.LocationDatabase
import org.lightquark.maptracker.data.db.LocationEntity
import java.util.UUID
import java.util.concurrent.ExecutorService

/**
 * Access point for database (MyLocation data) and location APIs (start/stop location updates and
 * checking location update status).
 */
class LocationRepository private constructor(
    private val locationDatabase: LocationDatabase,
    private val locationManager: LocationManager,
    private val executor: ExecutorService
) {

    // Database related fields/methods:
    private val locationDao = locationDatabase.locationDao()

    /**
     * Returns all recorded locations from database.
     */
    fun getLocations(): LiveData<List<LocationEntity>> = locationDao.getLocations()

    // Not being used now but could in future versions.
    /**
     * Returns specific location in database.
     */
    fun getLocation(id: UUID): LiveData<LocationEntity> = locationDao.getLocation(id)

    // Not being used now but could in future versions.
    /**
     * Updates location in database.
     */
    fun updateLocation(locationEntity: LocationEntity) {
        executor.execute {
            locationDao.updateLocation(locationEntity)
        }
    }

    /**
     * Adds location to the database.
     */
    fun addLocation(locationEntity: LocationEntity) {
        executor.execute {
            locationDao.addLocation(locationEntity)
        }
    }

    /**
     * Adds list of locations to the database.
     */
    fun addLocations(locationEntities: List<LocationEntity>) {
        executor.execute {
            locationDao.addLocations(locationEntities)
        }
    }

    /**
     * Adds list of locations to the database.
     */
    fun cleanupDatabase() {
        executor.execute {
            locationDatabase.clearAllTables()
        }
    }

    // Location related fields/methods:
    /**
     * Status of whether the app is actively subscribed to location changes.
     */
    val receivingLocationUpdates: LiveData<Boolean> = locationManager.receivingLocationUpdates

    /**
     * Subscribes to location updates.
     */
    @MainThread
    fun startLocationUpdates() = locationManager.startLocationUpdates()

    /**
     * Un-subscribes from location updates.
     */
    @MainThread
    fun stopLocationUpdates() = locationManager.stopLocationUpdates()

    companion object {
        @Volatile
        private var INSTANCE: LocationRepository? = null

        fun getInstance(context: Context, executor: ExecutorService): LocationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationRepository(
                    LocationDatabase.getInstance(context),
                    LocationManager.getInstance(context),
                    executor
                )
                    .also { INSTANCE = it }
            }
        }
    }
}