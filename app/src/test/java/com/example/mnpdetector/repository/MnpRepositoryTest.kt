package com.example.mnpdetector.repository

import com.example.mnpdetector.data.dao.MetadataDao
import com.example.mnpdetector.data.dao.MnpNumberDao
import com.example.mnpdetector.data.model.Metadata
import com.example.mnpdetector.data.model.MnpNumber
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Тесты MnpRepository с фейковыми DAO (без Android-зависимостей).
 */
class MnpRepositoryTest {

    private val mnpDao = FakeMnpNumberDao()
    private val metadataDao = FakeMetadataDao()
    private val repository = MnpRepository(mnpDao, metadataDao)

    @Test
    fun `getMnpNumberByNumber - найденный номер`() = runTest {
        mnpDao.storage["79000000001"] = MnpNumber("79000000001", "МТС")

        val result = repository.getMnpNumberByNumber("79000000001")

        assertEquals("МТС", result?.operator)
    }

    @Test
    fun `getMnpNumberByNumber - отсутствующий номер`() = runTest {
        assertNull(repository.getMnpNumberByNumber("79999999999"))
    }

    @Test
    fun `insertAll + count`() = runTest {
        repository.insertAllMnpNumbers(listOf(
            MnpNumber("79000000001", "МТС"),
            MnpNumber("79000000002", "Билайн"),
            MnpNumber("79000000003", "МегаФон")
        ))

        assertEquals(3, repository.getMnpNumbersCount())
    }

    @Test
    fun `clearAll - очистка таблицы`() = runTest {
        repository.insertAllMnpNumbers(listOf(MnpNumber("79000000001", "МТС")))
        repository.clearAllMnpNumbers()

        assertEquals(0, repository.getMnpNumbersCount())
    }

    @Test
    fun `метаданные - запись и чтение`() = runTest {
        repository.insertMetadata(Metadata("last_update", "2026-09-06"))

        val meta = repository.getMetadataByKey("last_update")
        assertEquals("2026-09-06", meta?.value)

        repository.deleteMetadataByKey("last_update")
        assertNull(repository.getMetadataByKey("last_update"))
    }

    // --- Фейки ---

    private class FakeMnpNumberDao : MnpNumberDao {
        val storage = mutableMapOf<String, MnpNumber>()

        override suspend fun getByNumber(number: String): MnpNumber? = storage[number]

        override suspend fun getByNumberPattern(numberPattern: String): List<MnpNumber> =
            storage.filterKeys { it.contains(numberPattern.removePrefix("%").removeSuffix("%")) }.values.toList()

        override suspend fun insertAll(numbers: List<MnpNumber>) {
            numbers.forEach { storage[it.number] = it }
        }

        override suspend fun insert(number: MnpNumber) {
            storage[number.number] = number
        }

        override suspend fun deleteAll() {
            storage.clear()
        }

        override suspend fun getCount(): Int = storage.size
    }

    private class FakeMetadataDao : MetadataDao {
        val storage = mutableMapOf<String, Metadata>()

        override suspend fun getByKey(key: String): Metadata? = storage[key]

        override suspend fun insert(metadata: Metadata) {
            storage[metadata.key] = metadata
        }

        override suspend fun deleteByKey(key: String) {
            storage.remove(key)
        }

        override suspend fun getAll(): List<Metadata> = storage.values.toList()
    }
}
