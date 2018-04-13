package com.daily.dailyhotel.util

inline fun String.takeNotEmpty(block: (String) -> Unit) {
    this.takeIf { it.isNotEmpty() && "null" != it }?.let { block(it) }
}

inline fun <T> Array<T>.takeNotEmpty(block: (Array<T>) -> Unit) {
    this.takeIf { it.isNotEmpty() }?.let { block(it) }
}

inline fun <T> Collection<T>.takeNotEmpty(block: (Collection<T>) -> Unit) {
    this.takeIf { it.isNotEmpty() }?.let { block(it) }
}

inline fun CharSequence?.isTextEmpty(): Boolean {
    return this?.trim()?.isEmpty()?.or("null".equals(this.trim().toString(), true)) ?: true
//    return this == null || this.trim().isEmpty() || "null" == this
}

inline fun isTextEmpty(vararg textArray: CharSequence?): Boolean {
    if (textArray.isEmpty()) return true
    for (text in textArray) if (text.isTextEmpty()) return true

    return false
}