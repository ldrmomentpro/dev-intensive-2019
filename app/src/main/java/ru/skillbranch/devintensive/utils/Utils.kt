package ru.skillbranch.devintensive.utils

import java.lang.StringBuilder
import java.util.*

object Utils {
    fun parseFullName(fullName: String?) : Pair<String?, String?> {

        val parts: List<String>? = fullName?.split(" ")
        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)

        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        val letters = mapOf(
            "А" to "A",
            "Б" to "B",
            "В" to "V",
            "Г" to "G",
            "Д" to "D",
            "Е" to "E",
            "Ё" to "E",
            "Ж" to "Zh",
            "З" to "Z",
            "И" to "I",
            "Й" to "I",
            "К" to "K",
            "Л" to "L",
            "М" to "M",
            "Н" to "N",
            "О" to "O",
            "П" to "P",
            "Р" to "R",
            "С" to "S",
            "Т" to "T",
            "У" to "U",
            "Ф" to "F",
            "Х" to "H",
            "Ц" to "C",
            "Ч" to "Ch",
            "Ш" to "Sh",
            "Щ" to "Sh'",
            "Ъ" to "",
            "Ы" to "I",
            "Ь" to "",
            "Э" to "E",
            "Ю" to "Yu",
            "Я" to "Ya",
            "а" to "a",
            "б" to "b",
            "в" to "v",
            "г" to "g",
            "д" to "d",
            "е" to "e",
            "ё" to "e",
            "ж" to "zh",
            "з" to "z",
            "и" to "i",
            "й" to "i",
            "к" to "k",
            "л" to "l",
            "м" to "m",
            "н" to "n",
            "о" to "o",
            "п" to "p",
            "р" to "r",
            "с" to "s",
            "т" to "t",
            "у" to "u",
            "ф" to "f",
            "х" to "h",
            "ц" to "c",
            "ч" to "ch",
            "ш" to "sh",
            "щ" to "sh'",
            "ъ" to "",
            "ы" to "i",
            "ь" to "",
            "э" to "e",
            "ю" to "yu",
            "я" to "ya"
        )

        val text = payload
        val sb = StringBuilder(text.length)
        for (i in 0 until text.length) {
            val l: String = payload.substring(i, i + 1)
            if (letters.containsKey(l)) {
                sb.append(letters[l])
            } else {
                sb.append(l)
            }
        }
        return sb.toString()
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val initFName: String?
        val initLName: String?
        if (firstName.isNullOrBlank() && lastName.isNullOrBlank()) {
        return "null"
        }
        else if (firstName.isNullOrBlank() && !lastName.isNullOrBlank()) {
            initLName = lastName.substring(0, 1)
            return "$initLName"
        }
        else if (!firstName.isNullOrBlank() && lastName.isNullOrBlank()) {
            initFName = firstName.substring(0, 1)
            return "$initFName"
        }
        else {
            initFName = firstName?.substring(0, 1)
            initLName = lastName?.substring(0, 1)
            return "$initFName$initLName"
        }
    }
}