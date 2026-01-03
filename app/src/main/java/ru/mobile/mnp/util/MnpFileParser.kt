package ru.mobile.mnp.util

import ru.mobile.mnp.data.model.MnpNumber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MnpFileParser {
    fun parse(inputStream: InputStream): List<MnpNumber> {
        val numbers = mutableListOf<MnpNumber>()
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.use { bufferedReader ->
            var line: String?
            while (run { line = bufferedReader.readLine(); line != null }) {
                val parts = line!!.split(";")
                if (parts.size >= 2) {
                    val number = parts[0].trim()
                    val operator = parts[1].trim()

                    // Only include the number and operator, ignore INN
                    if (number.isNotEmpty() && operator.isNotEmpty()) {
                        numbers.add(MnpNumber(number, operator))
                    }
                }
            }
        }

        return numbers
    }
    
    fun normalizePhoneNumber(phoneNumber: String): String {
        // Remove all non-digit characters
        val digitsOnly = phoneNumber.replace(Regex("[^\\d]"), "")
        
        // Handle different formats
        return when {
            digitsOnly.startsWith("7") && digitsOnly.length == 11 -> "7${digitsOnly.substring(1)}"
            digitsOnly.startsWith("8") && digitsOnly.length == 11 -> "7${digitsOnly.substring(1)}"
            digitsOnly.startsWith("7") && digitsOnly.length == 10 -> "7${digitsOnly}"
            digitsOnly.startsWith("8") && digitsOnly.length == 10 -> "7${digitsOnly.substring(1)}"
            digitsOnly.length == 10 -> "7${digitsOnly}"
            else -> digitsOnly
        }
    }
}