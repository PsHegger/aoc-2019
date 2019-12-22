package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.*
import io.github.pshegger.aoc2019.utils.permutations

fun day7(): Answer {
    val program = InputReader.read("day7.txt") {
        readLines().flatMap { line -> line.split(",").map { it.toInt() } }
    }

    val phaseSettings = (0..4).toList().permutations()
    val maxAmplification = phaseSettings.map { amplify(program, it) }.max() ?: throw AmplificationNotFoundError

    val feedbackPhaseSettings = (5..9).toList().permutations()
    val maxFeedbackAmplification = feedbackPhaseSettings
        .map { feedbackAmplified(program, it) }
        .max() ?: throw AmplificationNotFoundError

    return Answer.Full(maxAmplification, maxFeedbackAmplification)
}

private fun amplify(program: List<Int>, phaseSetting: List<Int>) =
    (0..4).fold(0) { outputSignal, index ->
        runProgram(program, listOf(phaseSetting[index], outputSignal)).output.first()
    }

private fun feedbackAmplified(program: List<Int>, phaseSetting: List<Int>): Int {
    val amplifiers = (0..4).fold(emptyList<ProgramState>()) { acc, index ->
        val prevOutput = acc.lastOrNull()?.output?.lastOrNull() ?: 0
        acc + runProgram(program, listOf(phaseSetting[index], prevOutput))
    }.toMutableList()

    var amplifierPtr = 0
    while (amplifiers[amplifierPtr].status != ProgramStatus.Terminated) {
        val prevAmplifier = (amplifierPtr - 1 + amplifiers.size) % amplifiers.size
        val prevOutput = amplifiers[prevAmplifier].output.last()
        amplifiers[amplifierPtr] = amplifiers[amplifierPtr].resume(listOf(prevOutput))
        amplifierPtr = (amplifierPtr + 1) % amplifiers.size
    }

    return amplifiers.last().output.last()
}

object AmplificationNotFoundError : Throwable()
