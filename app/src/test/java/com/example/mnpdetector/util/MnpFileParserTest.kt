package com.example.mnpdetector.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class MnpFileParserTest {

    private val parser = MnpFileParser()

    @Test
    fun `parse - корректные строки`() {
        val csv = "79000000001;МТС\n79000000002;Билайн\n79000000003;МегаФон\n"
        val result = parser.parse(ByteArrayInputStream(csv.toByteArray()))

        assertEquals(3, result.size)
        assertEquals("МТС", result[0].operator)
        assertEquals("79000000001", result[0].number)
        assertEquals("Билайн", result[1].operator)
        assertEquals("МегаФон", result[2].operator)
    }

    @Test
    fun `parse - пропуск пустых и битых строк`() {
        val csv = "79000000001;МТС\n\n;\n79000000002;\n;Билайн\n"
        val result = parser.parse(ByteArrayInputStream(csv.toByteArray()))

        assertEquals(1, result.size)
        assertEquals("79000000001", result[0].number)
    }

    @Test
    fun `normalizePhoneNumber - варианты форматов`() {
        // +7 (900) 000-00-01
        assertEquals("79000000001", parser.normalizePhoneNumber("+7 (900) 000-00-01"))
        // 8 900 000 00 01
        assertEquals("79000000001", parser.normalizePhoneNumber("8 900 000 00 01"))
        // 79000000001
        assertEquals("79000000001", parser.normalizePhoneNumber("79000000001"))
        // 9000000001 (10 цифр без кода страны)
        assertEquals("79000000001", parser.normalizePhoneNumber("9000000001"))
        // 89000000001 → 7
        assertEquals("79000000001", parser.normalizePhoneNumber("89000000001"))
    }

    @Test
    fun `normalizePhoneNumber - экзотика`() {
        // Короткий номер не трогаем
        assertEquals("112", parser.normalizePhoneNumber("112"))
        // Пустая строка
        assertEquals("", parser.normalizePhoneNumber(""))
        // Буквы удаляются → 11 цифр, уже валидный с 7: без изменений
        assertEquals("79000000001", parser.normalizePhoneNumber("79000000001abc"))
        // Кривой 12-значный — не трогаем
        assertEquals("790000000001", parser.normalizePhoneNumber("7abc900def00000001"))
    }
}
