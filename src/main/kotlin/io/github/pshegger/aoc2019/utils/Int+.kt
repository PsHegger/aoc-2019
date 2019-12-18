package io.github.pshegger.aoc2019.utils

fun Int.digitCount() =
    generateSequence(1) { it * 10 }
        .map { this % it }
        .takeWhile { it != this }
        .count()

fun Int.toDigits(): List<Int> = toString().map { it.toInt() - 48 }.reversed()
