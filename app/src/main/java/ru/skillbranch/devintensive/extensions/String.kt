package ru.skillbranch.devintensive.extensions

import android.text.Html

fun String.truncate(cut: Int = 16): String {

    val text = this.substring(0, cut) + "..."
    return text
}

