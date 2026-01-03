package ru.mobile.mnp.di

import android.app.Application
import ru.mobile.mnp.data.database.MnpDatabase
import ru.mobile.mnp.repository.MnpRepository
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