package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.Answer
import io.github.pshegger.aoc2019.InputReader

fun day2(): Answer {
    val input = InputReader.read("day2.txt") {
        readLines().flatMap { line -> line.split(",").map { it.toInt() } }
    }

    val program = input.updateWithParameters(12, 2)
    val result = runProgram(program)
    val (noun, verb) = findParameters(input, 19690720)

    return Answer.Full(result[0], 100 * noun + verb)
}

private fun findParameters(input: List<Int>, targetResult: Int): Pair<Int, Int> {
    for (noun in (0..99)) {
        for (verb in (0..99)) {
            val program = input.updateWithParameters(noun, verb)
            val result = runProgram(program)[0]
            if (result == targetResult) {
                return Pair(noun, verb)
            }
        }
    }

    throw NoSolutionFoundException
}

private tailrec fun runProgram(memory: List<Int>, pc: Int = 0): List<Int> = when (memory[pc]) {
    1 -> {
        val opResult = memory[memory[pc + 1]] + memory[memory[pc + 2]]
        runProgram(memory.withUpdatedValue(memory[pc + 3], opResult), pc + 4)
    }
    2 -> {
        val opResult = memory[memory[pc + 1]] * memory[memory[pc + 2]]
        runProgram(memory.withUpdatedValue(memory[pc + 3], opResult), pc + 4)
    }
    99 -> memory
    else -> throw UnknownOperatorException(memory[pc], pc)
}

private fun <T> List<T>.updateWithParameters(noun: T, verb: T) = withUpdatedValue(1, noun).withUpdatedValue(2, verb)

private fun <T> List<T>.withUpdatedValue(position: Int, newValue: T): List<T> {
    val prevElements = take(position)
    val nextElements = drop(position + 1)

    return prevElements + newValue + nextElements
}

private object NoSolutionFoundException : Throwable()

private class UnknownOperatorException(operator: Int, position: Int) : Throwable() {
    override val message = "Unknown operator ($operator) at position $position"
}
