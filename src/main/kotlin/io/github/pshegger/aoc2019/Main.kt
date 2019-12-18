package io.github.pshegger.aoc2019

import io.github.pshegger.aoc2019.days.*

object Main {

    private val DAY_MAPPING = mapOf<Int, () -> Answer>(
        1 to ::day1,
        2 to ::day2,
        3 to ::day3,
        4 to ::day4,
        5 to ::day5
    )

    @JvmStatic
    fun main(args: Array<String>) {
        println("Advent of Code 2019")
        println("Day to solve")
        val day = readLine()?.trim()?.toIntOrNull()

        if (day == null) {
            println("Cannot parse input")
            return
        }

        val solver = DAY_MAPPING[day]
        if (solver == null) {
            println("Solution not available for day $day")
            return
        }

        val answer = solver()
        println(answer)
    }
}
