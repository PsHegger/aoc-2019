package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.Answer
import io.github.pshegger.aoc2019.InputReader

fun day1() : Answer {
    val lines = InputReader.read("day1.txt") {
        readLines().map { it.toInt() }
    }

    val simpleFuel = lines.map { (it / 3) - 2 }.sum()
    val fullFuel = lines.map { fullFuelConsumption(it) }.sum()

    return Answer.Full(simpleFuel, fullFuel)
}

private tailrec fun fullFuelConsumption(mass: Int, prevFuel: Int = 0): Int {
    val requiredFuel = (mass / 3) - 2

    return if (requiredFuel <= 0) {
        prevFuel
    } else {
        fullFuelConsumption(requiredFuel, prevFuel + requiredFuel)
    }
}
