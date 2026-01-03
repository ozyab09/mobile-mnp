package com.example.mnpdetector.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mnpdetector.repository.MnpRepository
import com.example.mnpdetector.util.PreferencesHelper
import com.example.mnpdetector.util.ZipHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@HiltWorker
class DatabaseUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: MnpRepository,
    private val preferencesHelper: PreferencesHelper,
    private val zipHelper: ZipHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val sourceUrl = preferencesHelper.getSourceUrl()

            // Download the file
            val downloadedFile = zipHelper.downloadFile(sourceUrl)

            if (downloadedFile != null) {
                // Extract and parse the data
                val mnpNumbers = zipHelper.extractAndParseMnpData(downloadedFile)

                // Clear existing data and insert new data in a transaction
                repository.clearAllMnpNumbers()
                repository.insertAllMnpNumbers(mnpNumbers)

                // Update metadata
                repository.insertMetadata(com.example.mnpdetector.data.model.Metadata(
                    "last_update",
                    Date().toString()
                ))
                repository.insertMetadata(com.example.mnpdetector.data.model.Metadata(
                    "source_url",
                    sourceUrl
                ))
                repository.insertMetadata(com.example.mnpdetector.data.model.Metadata(
                    "record_count",
                    mnpNumbers.size.toString()
                ))

                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}