package com.daily.dailyhotel.util

inline fun String?.takeNotEmpty(block: (String) -> Unit) {
    this?.takeIf { !it.isTextEmpty() }?.let { block(it) }
}

inline fun <R> String?.letNotEmpty(block: (String) -> R): R? {
    return this?.takeIf { !it.isTextEmpty() }?.let { block(it) }
}

inline fun Int?.takeGreaterThanZero(block: (Int) -> Unit) {
    this?.takeIf { it > 0 }?.let { block(it) }
}

inline fun <T> Array<T>?.takeNotEmpty(block: (Array<T>) -> Unit) {
    this?.takeIf { it.isNotEmpty() }?.let { it }
}

inline fun <T> List<T>?.takeNotEmpty(block: (List<T>) -> Unit) {
    this?.takeIf { it.isNotEmpty() }?.let { it }
}

inline fun <T> T?.letReturnTrueElseReturnFalse(block: (T) -> Unit): Boolean {
    return this?.let { block(it); return true } == true
}

fun <T> List<T>?.isNotNullAndNotEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}

inline fun <T> T?.filterIf(block: (T) -> Boolean, defaultNull: Boolean = false): Boolean {
    return this?.let { block(it) } ?: defaultNull
}

inline fun <R> Boolean?.runTrue(block: () -> R?): R? {
    return this.takeIf { it == true }?.let { block() }
}

inline fun <R> Boolean?.runFalse(block: () -> R?): R? {
    return this.takeIf { it == false }?.let { block() }
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