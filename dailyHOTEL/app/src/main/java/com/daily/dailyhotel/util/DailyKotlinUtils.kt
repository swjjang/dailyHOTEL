package com.daily.dailyhotel.util

inline fun String?.takeNotEmpty(block: (String) -> Unit) {
    this?.takeIf { it.isNotEmpty() && "null" != it }?.let { block(it) }
}

inline fun <T> Collection<T>?.takeNotEmpty(block: (Collection<T>) -> Unit) {
    this?.takeIf { it.isNotEmpty() }?.let { block(it) }
}

fun CharSequence?.isTextEmpty(): Boolean {
    return this == null || isBlank() || "null" == trim()
}

fun isTextEmpty(vararg textArray: CharSequence?): Boolean {
    return textArray.any { it.isTextEmpty() }
}