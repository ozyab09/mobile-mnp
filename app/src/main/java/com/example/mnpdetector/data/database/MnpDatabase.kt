package com.example.mnpdetector.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mnpdetector.data.dao.MnpNumberDao
import com.example.mnpdetector.data.dao.MetadataDao
import com.example.mnpdetector.data.model.MnpNumber
import com.example.mnpdetector.data.model.Metadata
import com.example.mnpdetector.util.Converters
import android.content.Context

@Database(
    entities = [MnpNumber::class, Metadata::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MnpDatabase : RoomDatabase() {
    abstract fun mnpNumberDao(): MnpNumberDao
    abstract fun metadataDao(): MetadataDao

    companion object {
        @Volatile
        private var INSTANCE: MnpDatabase? = null

        fun getDatabase(context: Context): MnpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MnpDatabase::class.java,
                    "mnp_database"
                )
                .fallbackToDestructiveMigration() // Recreate database if migration is needed
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}