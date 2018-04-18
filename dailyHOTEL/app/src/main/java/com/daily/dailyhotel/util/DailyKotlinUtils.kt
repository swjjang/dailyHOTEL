package com.daily.dailyhotel.util

inline fun String?.takeNotEmpty(block: (String) -> Unit) {
    this?.takeIf { !it.isTextEmpty() }?.let { block(it) }
}

inline fun <R> String?.letNotEmpty(block: (String) -> R): R? {
    return this?.takeIf { !it.isTextEmpty() }?.let { block(it) }
}

inline fun <T> Collection<T>?.takeNotEmpty(block: (Collection<T>) -> Unit) {
    this?.takeIf { it.isNotEmpty() }?.let { block(it) }
}

fun CharSequence?.isTextEmpty(): Boolean {
    return this == null || isBlank() || this == "null"
}

fun isTextEmpty(vararg textArray: CharSequence?): Boolean {
    return textArray.any { it.isTextEmpty() }
}