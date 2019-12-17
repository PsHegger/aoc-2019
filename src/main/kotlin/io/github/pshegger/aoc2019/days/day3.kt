package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.Answer
import io.github.pshegger.aoc2019.InputReader
import kotlin.math.abs

private typealias Field = MutableMap<Coordinate, List<StepCount>>
private typealias MovementFoldData = Triple<Coordinate, Field, Int>

fun day3(): Answer {
    val field = InputReader.read("day3.txt") {
        readLines().mapIndexed { index, line ->
            val movements = line.split(",")
                .map { Movement.parse(it) }
            Wire(index, movements)
        }.fold(mutableMapOf<Coordinate, List<StepCount>>()) { field, wire ->
            wire.simulateMovements(field)
        }
    }.toMap()

    val smallestManhattanDistance = field
        .filter { it.value.size > 1 }
        .map { it.key }
        .filterNot { it == Coordinate(0, 0) }
        .minBy { it.manhattanDistance }
        ?.manhattanDistance ?: throw NoIntersectionException

    val leastSteps = field
        .filter { it.value.size > 1 }
        .filterNot { it.key == Coordinate(0, 0) }
        .map { it.value }
        .minBy { stepCounts -> stepCounts.sumBy { it.count } }
        ?.sumBy { it.count } ?: throw NoIntersectionException

    return Answer.Full(smallestManhattanDistance, leastSteps)
}

private data class StepCount(val wireId: Int, val count: Int)

private data class Wire(val id: Int, val movements: List<Movement>) {

    fun simulateMovements(field: Field): Field =
        movements.fold(MovementFoldData(Coordinate(0, 0), field, 0)) { (coords, field, steps), movement ->
            movement.simulateMovement(id, coords, field, steps)
        }.second
}

private data class Coordinate(val x: Int, val y: Int) {
    val manhattanDistance = abs(x) + abs(y)
}

private enum class Direction(val notation: String) {
    Up("U"),
    Right("R"),
    Down("D"),
    Left("L");

    companion object {

        fun fromNotation(notation: String) =
            values().firstOrNull { it.notation == notation } ?: throw UnknownDirectionException(notation)
    }
}

private data class Movement(val direction: Direction, val distance: Int) {

    tailrec fun simulateMovement(
        wireId: Int,
        currentCoords: Coordinate,
        field: Field,
        sumSteps: Int,
        stepsRemaining: Int = distance
    ): MovementFoldData {
        val (x, y) = currentCoords
        if (stepsRemaining == 0) {
            field.addWire(wireId, x, y, sumSteps)
            return MovementFoldData(currentCoords, field, sumSteps)
        }

        val nextCoord = when (direction) {
            Direction.Up -> Coordinate(x, y + 1)
            Direction.Right -> Coordinate(x + 1, y)
            Direction.Down -> Coordinate(x, y - 1)
            Direction.Left -> Coordinate(x - 1, y)
        }

        field.addWire(wireId, x, y, sumSteps)
        return simulateMovement(wireId, nextCoord, field, sumSteps + 1, stepsRemaining - 1)
    }

    private fun Field.addWire(wireId: Int, x: Int, y: Int, stepCount: Int) {
        val coords = Coordinate(x, y)
        val currentSteps = get(coords)
        if (currentSteps == null) {
            this[coords] = listOf(StepCount(wireId, stepCount))
        } else if (currentSteps.none { it.wireId == wireId } ){
            this[coords] = currentSteps + StepCount(wireId, stepCount)
        }
    }

    companion object {

        fun parse(notation: String): Movement {
            val direction = Direction.fromNotation(notation.take(1))
            val distance = notation.drop(1).toInt()
            return Movement(direction, distance)
        }
    }
}

private object NoIntersectionException : Throwable()

private class UnknownDirectionException(direction: String) : Throwable() {
    override val message = "Unknown direction: $direction"
}
