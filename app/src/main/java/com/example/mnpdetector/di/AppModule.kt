package com.example.mnpdetector.di

import android.app.Application
import com.example.mnpdetector.data.database.MnpDatabase
import com.example.mnpdetector.repository.MnpRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMnpDatabase(application: Application): MnpDatabase {
        return MnpDatabase.getDatabase(application)
    }

    @Provides
    @Singleton
    fun provideMnpRepository(database: MnpDatabase): MnpRepository {
        return MnpRepository(
            database.mnpNumberDao(),
            database.metadataDao()
        )
    }
}