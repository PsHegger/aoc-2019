package io.github.pshegger.aoc2019

import io.github.pshegger.aoc2019.utils.withUpdatedValue
import kotlin.math.pow

fun evalProgram(program: List<Int>, inputs: List<Int> = emptyList()): ProgramState {
    tailrec fun go(state: ProgramState): ProgramState {
        if (state.terminated) {
            return state
        }

        val operator = OPERATORS.firstOrNull { it.opCode == state.opCode }
            ?: throw UnknownOperatorException(state.opCode, state.pc)
        return go(operator(state))
    }

    return go(ProgramState(program, 0, inputs, emptyList()))
}

private val OPERATORS = listOf(
    simpleOperator(1, 3) { state ->
        val opResult = state.opVal(1) + state.opVal(2)
        state.updateMemory(state.op(3), opResult)
    },
    simpleOperator(2, 3) { state ->
        val opResult = state.opVal(1) * state.opVal(2)
        state.updateMemory(state.op(3), opResult)
    },
    simpleOperator(3, 1) { state ->
        state.readValue(state.op(1))
    },
    simpleOperator(4, 1) { state ->
        state.writeValue(state.opVal(1))
    },
    Operator(5, 2) { state ->
        if (state.opVal(1) != 0) {
            Pair(state.setPc(state.opVal(2)), false)
        } else {
            Pair(state, true)
        }
    },
    Operator(6, 2) { state ->
        if (state.opVal(1) == 0) {
            Pair(state.setPc(state.opVal(2)), false)
        } else {
            Pair(state, true)
        }
    },
    simpleOperator(7, 3) { state ->
        val opResult = if (state.opVal(1) < state.opVal(2)) 1 else 0
        state.updateMemory(state.op(3), opResult)
    },
    simpleOperator(8, 3) { state ->
        val opResult = if (state.opVal(1) == state.opVal(2)) 1 else 0
        state.updateMemory(state.op(3), opResult)
    },
    simpleOperator(99, 0) { state -> state.terminate() }
)

private fun simpleOperator(opCode: Int, opCount: Int, perform: (ProgramState) -> ProgramState) =
    Operator(opCode, opCount) { state ->
        Pair(perform(state), true)
    }

private data class Operator(
    val opCode: Int,
    val opCount: Int,
    private val perform: (ProgramState) -> Pair<ProgramState, Boolean>
) {

    operator fun invoke(state: ProgramState): ProgramState =
        perform(state).let { (newState, increasePc) ->
            if (increasePc) {
                newState.advancePc(opCount + 1)
            } else {
                newState
            }
        }
}

data class ProgramState(
    private val memory: List<Int>,
    val pc: Int,
    val inputs: List<Int>,
    val output: List<Int>,
    val terminated: Boolean = false
) {

    val opCode: Int
        get() = memory[pc] % 100

    fun updateMemory(address: Int, value: Int) = copy(memory = memory.withUpdatedValue(address, value))
    fun terminate() = copy(terminated = true)
    fun advancePc(steps: Int) = copy(pc = pc + steps)
    fun setPc(address: Int) = copy(pc = address)
    fun readValue(targetAddress: Int) =
        copy(memory = memory.withUpdatedValue(targetAddress, inputs.first()), inputs = inputs.drop(1))

    fun writeValue(value: Int) =
        copy(output = output + value)

    fun op(n: Int) = memory[pc + n]
    fun opVal(n: Int) = when (opModeN(n)) {
        OpMode.Position -> memory[op(n)]
        OpMode.Immediate -> op(n)
    }

    operator fun get(address: Int) = memory[address]

    private fun opModeN(n: Int): OpMode {
        val low = 10.0.pow(n + 1).toInt()
        val high = low * 10
        return if ((memory[pc] % high / low) == 1) {
            OpMode.Immediate
        } else {
            OpMode.Position
        }
    }

    private enum class OpMode {
        Position, Immediate
    }
}

private class UnknownOperatorException(operator: Int, position: Int) : Throwable() {
    override val message = "Unknown operator ($operator) at position $position"
}
