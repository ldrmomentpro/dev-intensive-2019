package ru.skillbranch.devintensive.extensions


fun String.truncate(cut: Int = 16): String {

    val text = this.substring(0, cut) + "..."
    return text
}

fun String.stripHtml() = this
    .replace(Regex("<[^>]*>"), "")
    .replace(Regex("&amp;|&lt;|&gt;|&quot;|&apos;|&#\\d+;"), "")
    .replace(Regex(" +"), " ")