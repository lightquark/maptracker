package org.lightquark.maptracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.UUID

/**
 * Defines database operations.
 */
@Dao
interface LocationDao {

    @Query("SELECT * FROM location ORDER BY timestamp DESC LIMIT 1000")
    fun getLocations(): LiveData<List<LocationEntity>>

    @Query("SELECT * FROM location WHERE id=(:id)")
    fun getLocation(id: UUID): LiveData<LocationEntity>

    @Update
    fun updateLocation(locationEntity: LocationEntity)

    @Insert
    fun addLocation(locationEntity: LocationEntity)

    @Insert
    fun addLocations(myLocationEntities: List<LocationEntity>)
}
