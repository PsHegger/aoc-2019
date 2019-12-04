package io.github.pshegger.aoc2019

object Main {

    private val DAY_MAPPING = mapOf<Int, () -> Answer>(
        1 to ::day1
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