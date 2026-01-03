package com.example.mnpdetector.repository

import com.example.mnpdetector.data.dao.MnpNumberDao
import com.example.mnpdetector.data.dao.MetadataDao
import com.example.mnpdetector.data.model.MnpNumber
import com.example.mnpdetector.data.model.Metadata
import javax.inject.Inject

class MnpRepository @Inject constructor(
    private val mnpNumberDao: MnpNumberDao,
    private val metadataDao: MetadataDao
) {
    suspend fun getMnpNumberByNumber(number: String): MnpNumber? {
        return mnpNumberDao.getByNumber(number)
    }
    
    suspend fun insertMnpNumber(number: MnpNumber) {
        mnpNumberDao.insert(number)
    }
    
    suspend fun insertAllMnpNumbers(numbers: List<MnpNumber>) {
        mnpNumberDao.insertAll(numbers)
    }
    
    suspend fun clearAllMnpNumbers() {
        mnpNumberDao.deleteAll()
    }
    
    suspend fun getMnpNumbersCount(): Int {
        return mnpNumberDao.getCount()
    }
    
    suspend fun getMetadataByKey(key: String): Metadata? {
        return metadataDao.getByKey(key)
    }
    
    suspend fun insertMetadata(metadata: Metadata) {
        metadataDao.insert(metadata)
    }
    
    suspend fun deleteMetadataByKey(key: String) {
        metadataDao.deleteByKey(key)
    }
    
    suspend fun getAllMetadata(): List<Metadata> {
        return metadataDao.getAll()
    }
}