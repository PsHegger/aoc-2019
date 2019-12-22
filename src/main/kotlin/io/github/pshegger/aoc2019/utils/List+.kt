package io.github.pshegger.aoc2019.utils

fun <T> List<T>.withUpdatedValue(position: Int, newValue: T): List<T> {
    val prevElements = take(position)
    val nextElements = drop(position + 1)

    return prevElements + newValue + nextElements
}

fun <T> List<T>.permutations(): List<List<T>> {
    if (size == 1) {
        return listOf(this)
    }

    return flatMap { t ->
        (this - t).permutations().map { listOf(t) + it }
    }
}
