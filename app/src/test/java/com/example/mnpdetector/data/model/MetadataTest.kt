package com.example.mnpdetector.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MetadataTest {

    @Test
    fun `равенство - одинаковые значения`() {
        val a = Metadata("key1", "value1")
        val b = Metadata("key1", "value1")
        assertEquals(a, b)
    }

    @Test
    fun `равенство - разные значения`() {
        val a = Metadata("key1", "value1")
        val b = Metadata("key1", "value2")
        assertNotEquals(a, b)
    }
}
