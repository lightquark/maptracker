package org.lightquark.maptracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

/**
 * Data class for Location related data (only takes what's needed from {@link android.location.Location} class).
 */
@Entity(tableName = "location")
data class LocationEntity(

    @PrimaryKey val id: UUID = UUID.randomUUID(),

    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Instant = Instant.MIN,
    val altitude: Double = 0.0,
    val mslAltitude: Double = 0.0,
    val accuracy: Float = 0.0f,
    val speed: Float = 0.0f,

    // true, if location is captured when the app was in foreground
    val foreground: Boolean = false,

    // true, if location is captured with ACCESS_FINE_LOCATION permission
    val precision: Boolean = false,
) {

    override fun toString(): String {
        val appState = if (foreground) {
            "in app"
        } else {
            "in BG"
        }

        return "$latitude, $longitude $appState on $timestamp.\n"
    }
}
