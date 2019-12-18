package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.Answer
import io.github.pshegger.aoc2019.InputReader
import io.github.pshegger.aoc2019.evalProgram

fun day5(): Answer {
    val input = InputReader.read("day5.txt") {
        readLines().flatMap { line -> line.split(",").map { it.toInt() } }
    }

    val airConditionerResult = evalProgram(input, listOf(1))
    val airConditionerDiagnosticCode = if (airConditionerResult.output.dropLast(1).all { it == 0 }) {
        airConditionerResult.output.last().toString()
    } else {
        "Error: ${airConditionerResult.output.dropLast(1)}"
    }

    val thermalRadiatorDiagnosticCode = evalProgram(input, listOf(5)).output.first()

    return Answer.Full(airConditionerDiagnosticCode, thermalRadiatorDiagnosticCode)
}
