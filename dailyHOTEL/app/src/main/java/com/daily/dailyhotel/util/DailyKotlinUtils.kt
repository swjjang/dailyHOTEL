package com.daily.dailyhotel.util

inline fun String?.takeNotEmpty(block: (String) -> Unit) {
    this?.takeIf { !it.isTextEmpty() }?.let { block(it) }
}

inline fun <R> String?.letNotEmpty(block: (String) -> R): R? {
    return this?.takeIf { !it.isTextEmpty() }?.let { block(it) }
}

inline fun <T> T?.filterIf(block: (T) -> Boolean, defaultNull: Boolean = false): Boolean {
    return if (this == null) defaultNull else this.let { block(it) }
}

inline fun <R> Boolean?.runTrue(block: () -> R?): R? {
    return this.takeIf { it == true }?.let { block() }
}

fun CharSequence?.isTextEmpty(): Boolean {
    return this == null || isBlank() || this == "null"
}

fun isTextEmpty(vararg textArray: CharSequence?): Boolean {
    return textArray.any { it.isTextEmpty() }
}

fun String?.takeNotEmptyThisAddStringButDefaultString(defaultString: String, addString: String?): String {
    return if (isTextEmpty()) defaultString else this!! + addString.takeNotEmptyThisAddStringButDefaultString("", null)
}