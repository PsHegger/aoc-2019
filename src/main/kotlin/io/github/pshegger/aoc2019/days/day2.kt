package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.Answer
import io.github.pshegger.aoc2019.InputReader
import io.github.pshegger.aoc2019.runProgram
import io.github.pshegger.aoc2019.utils.withUpdatedValue

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

private fun <T> List<T>.updateWithParameters(noun: T, verb: T) = withUpdatedValue(1, noun).withUpdatedValue(2, verb)

private object NoSolutionFoundException : Throwable()
