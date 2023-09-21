package org.lightquark.maptracker.data.db

import androidx.room.TypeConverter
import java.time.Instant
import java.util.UUID

/**
 * Converts non-standard objects in the {@link Location} data class into and out of the database.
 */
class LocationTypeConverters {

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(millisSinceEpoch: Long?): Instant? {
        return millisSinceEpoch?.let {
            Instant.ofEpochMilli(it)
        }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
}
