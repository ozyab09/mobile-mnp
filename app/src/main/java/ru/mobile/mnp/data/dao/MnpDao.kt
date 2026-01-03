package ru.mobile.mnp.data.dao

import androidx.room.*
import ru.mobile.mnp.data.model.MnpNumber
import ru.mobile.mnp.data.model.Metadata
import kotlinx.coroutines.flow.Flow

@Dao
interface MnpNumberDao {
    @Query("SELECT * FROM mnp_numbers WHERE number = :number")
    suspend fun getByNumber(number: String): MnpNumber?

    @Query("SELECT * FROM mnp_numbers WHERE number LIKE :numberPattern")
    suspend fun getByNumberPattern(numberPattern: String): List<MnpNumber>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(numbers: List<MnpNumber>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(number: MnpNumber)

    @Query("DELETE FROM mnp_numbers")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM mnp_numbers")
    suspend fun getCount(): Int
}

@Dao
interface MetadataDao {
    @Query("SELECT * FROM metadata WHERE key = :key")
    suspend fun getByKey(key: String): Metadata?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: Metadata)

    @Query("DELETE FROM metadata WHERE key = :key")
    suspend fun deleteByKey(key: String)

    @Query("SELECT * FROM metadata")
    suspend fun getAll(): List<Metadata>
}