package com.example.mnpdetector.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `fromTimestamp - round trip`() {
        val date = Date(1694000000000L)
        val timestamp = converters.dateToTimestamp(date)
        assertEquals(date, converters.fromTimestamp(timestamp))
    }

    @Test
    fun `dateToTimestamp - null`() {
        assertNull(converters.dateToTimestamp(null))
    }

    @Test
    fun `fromTimestamp - null`() {
        assertNull(converters.fromTimestamp(null))
    }
}
