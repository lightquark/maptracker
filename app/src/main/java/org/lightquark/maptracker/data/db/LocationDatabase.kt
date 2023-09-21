package org.lightquark.maptracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Database for storing all location data.
 */
@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
@TypeConverters(LocationTypeConverters::class)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {

        private const val DATABASE_NAME = "map-tracker-database"

        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getInstance(context: Context): LocationDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): LocationDatabase {
            return Room.databaseBuilder(
                context,
                LocationDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
