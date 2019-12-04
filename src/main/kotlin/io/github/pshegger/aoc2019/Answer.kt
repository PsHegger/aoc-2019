package io.github.pshegger.aoc2019

sealed class Answer {
    abstract val part1: String
    abstract val part2: String

    object Empty : Answer() {
        override val part1 = "<unknown>"
        override val part2 = "<unknown>"
    }

    class Part1(p1: Any) : Answer() {
        override val part1 = p1.toString()
        override val part2 = "<unknown>"
    }

    class Full(p1: Any, p2: Any) : Answer() {
        override val part1 = p1.toString()
        override val part2 = p2.toString()
    }

    override fun toString(): String = buildString {
        append("Part 1: ")
        append(part1)
        append("\n")
        append("Part 2: ")
        append(part2)
    }
}
