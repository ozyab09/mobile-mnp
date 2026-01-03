package com.example.mnpdetector.util

import android.content.Context
import com.example.mnpdetector.data.model.MnpNumber
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZipHelper @Inject constructor(@ApplicationContext private val context: Context) {
    
    private val client = OkHttpClient()
    
    suspend fun downloadFile(url: String): File? {
        return try {
            val request = Request.Builder()
                .url(url)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val file = File(context.cacheDir, "mnp_data.zip")
                val outputStream = FileOutputStream(file)
                response.body?.byteStream()?.copyTo(outputStream)
                outputStream.close()
                
                file
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun extractAndParseMnpData(zipFile: File): List<MnpNumber> {
        val numbers = mutableListOf<MnpNumber>()
        val parser = MnpFileParser()
        
        ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
            var entry = zipInputStream.nextEntry
            while (entry != null) {
                if (!entry.isDirectory && (entry.name.endsWith(".txt") || entry.name.endsWith(".csv"))) {
                    // Parse the file content
                    val content = zipInputStream.bufferedReader().readText()
                    val inputStream = content.byteInputStream()
                    numbers.addAll(parser.parse(inputStream))
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
        }
        
        return numbers
    }
}