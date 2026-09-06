package com.example.mnpdetector.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MnpNumberTest {

    @Test
    fun `равенство - одинаковые номера`() {
        assertEquals(
            MnpNumber("79000000001", "МТС"),
            MnpNumber("79000000001", "МТС")
        )
    }

    @Test
    fun `равенство - разные операторы`() {
        assertNotEquals(
            MnpNumber("79000000001", "МТС"),
            MnpNumber("79000000001", "Билайн")
        )
    }

    @Test
    fun `номер является первичным ключом`() {
        val a = MnpNumber("79000000001", "МТС")
        val b = MnpNumber("79000000001", "Билайн")
        assertEquals(a.number, b.number)
    }
}
