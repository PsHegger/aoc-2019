package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.Answer
import io.github.pshegger.aoc2019.InputReader

fun day6(): Answer {
    val orbits = InputReader.read("day6.txt") {
        readLines().map { edge ->
            val (start, end) = edge.split(")", limit = 2)
            Pair(start, end)
        }
    }

    val orbitCounts = findOrbitCounts(orbits)
    val orbitCountChecksum = orbitCounts.values.sum()

    val pathToMe = backtrack(orbits, "YOU", "COM")
    val pathToSanta = backtrack(orbits, "SAN", "COM")

    val commonRoot = pathToMe.first { it in pathToSanta }

    val commonRootDistance = orbitCounts[commonRoot] ?: throw RouteNotFoundException
    val myDistance = orbitCounts["YOU"] ?: throw RouteNotFoundException
    val santaDistance = orbitCounts["SAN"] ?: throw RouteNotFoundException
    val transferCount = (myDistance + santaDistance) - 2 * commonRootDistance - 2

    return Answer.Full(orbitCountChecksum, transferCount)
}

private fun findOrbitCounts(orbits: List<Pair<String, String>>): Map<String, Int> {

    tailrec fun go(consideredPlanets: List<String>, orbitCounts: Map<String, Int>): Map<String, Int> {
        if (consideredPlanets.isEmpty()) {
            return orbitCounts
        }

        val planet = consideredPlanets.first()
        val parent = orbits.first { it.second == planet }.first
        val parentCount = orbitCounts[parent] ?: 0
        val newConsiderations = orbits.filter { it.first == planet }.map { it.second }
        val updatedCounts = orbitCounts + mapOf(planet to parentCount + 1)
        return go(consideredPlanets.drop(1) + newConsiderations, updatedCounts)
    }

    return go(orbits.filter { it.first == "COM" }.map { it.second }, mapOf("COM" to 0))
}

private tailrec fun backtrack(
    orbits: List<Pair<String, String>>,
    start: String,
    end: String,
    path: List<String> = emptyList()
): List<String> {
    if (start == end) {
        return path + end
    }

    val previous = orbits.first { it.second == start }.first
    return backtrack(orbits, previous, end, path + start)
}

object RouteNotFoundException : Throwable()
