package com.daily.dailyhotel.util

inline fun String.takeNotEmpty(block: (String) -> Unit) {
    this.takeIf { it.isNotEmpty() }?.let { block(it) }
}

inline fun <T> Array<T>.takeNotEmpty(block: (Array<T>) -> Unit) {
    this.takeIf { it.isNotEmpty() }?.let { block(it) }
}

inline fun <T> Collection<T>.takeNotEmpty(block: (Collection<T>) -> Unit) {
    this.takeIf { it.isNotEmpty() }?.let { block(it) }
}