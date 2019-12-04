package io.github.pshegger.aoc2019

import io.github.pshegger.aoc2019.utils.withUpdatedValue

tailrec fun evalProgram(program: List<Int>, pc: Int = 0): List<Int> {
    val operator = OPERATORS.firstOrNull { it.opCode == program[pc] }
        ?: throw UnknownOperatorException(program[pc], pc)

    return if (operator.haltTermination) {
        program
    } else {
        evalProgram(operator(program, pc), pc + operator.opCount + 1)
    }
}

private val OPERATORS = listOf(
    Operator(1, 3) { memory, pc ->
        val opResult = memory[memory[pc + 1]] + memory[memory[pc + 2]]
        memory.withUpdatedValue(memory[pc + 3], opResult)
    },
    Operator(2, 3) { memory, pc ->
        val opResult = memory[memory[pc + 1]] * memory[memory[pc + 2]]
        memory.withUpdatedValue(memory[pc + 3], opResult)
    },
    Operator(99, 0, haltTermination = true) { memory, _ -> memory }
)

private data class Operator(
    val opCode: Int,
    val opCount: Int,
    val haltTermination: Boolean = false,
    private val perform: (List<Int>, Int) -> List<Int>
) {
    operator fun invoke(memory: List<Int>, pc: Int): List<Int> = perform(memory, pc)
}

private class UnknownOperatorException(operator: Int, position: Int) : Throwable() {
    override val message = "Unknown operator ($operator) at position $position"
}
