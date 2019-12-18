package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.Answer

private typealias NumberValidator = (Int) -> Boolean

fun day4(): Answer {
    val (low, high) = Pair(359282, 820401)

    val validator = ValidatorSet(adjacencyValidator, monotonicityValidator)
    val possiblePasswordCount = (low..high)
        .count { validator.validate(it) }

    val part2Validator = ValidatorSet(nonGroupedAdjacencyValidator, monotonicityValidator)
    val part2PossiblePasswordCount = (low..high)
        .count { part2Validator.validate(it) }
    
    return Answer.Full(possiblePasswordCount, part2PossiblePasswordCount)
}

private class ValidatorSet(vararg val validators: NumberValidator) {

    fun validate(number: Int) =
        validators.all { it(number) }
}

private data class GroupSize(val item: Int, val count: Int)

private val adjacencyValidator = { number: Int ->
    val digits = number.toDigits()
    val digitCount = number.digitCount()
    (1 until digitCount).any { i -> digits[i] == digits[i - 1] }
}

private val monotonicityValidator = { number: Int ->
    val digits = number.toDigits()
    digits.dropLast(1).foldIndexed(true) { index, isValid, digit ->
        isValid && (digits[index] >= digits[index + 1])
    }
}

private val nonGroupedAdjacencyValidator = { number: Int ->
    val groups = number.toDigits().fold(emptyList<GroupSize>()) { groups, digit ->
        val last = groups.lastOrNull()
        if (last == null || last.item != digit) {
            groups + GroupSize(digit, 1)
        } else {
            groups.dropLast(1) + last.copy(count = last.count + 1)
        }
    }
    groups.any { it.count == 2 }
}

private fun Int.digitCount() =
    generateSequence(1) { it * 10 }
        .map { this % it }
        .takeWhile { it != this }
        .count()

private fun Int.toDigits(): List<Int> = toString().map { it.toInt() - 48 }.reversed()
