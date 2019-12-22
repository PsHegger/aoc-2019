package io.github.pshegger.aoc2019

import io.github.pshegger.aoc2019.utils.withUpdatedValue
import kotlin.math.pow

fun runProgram(program: List<Int>, inputs: List<Int> = emptyList()) =
    evalProgram(ProgramState(program, 0, inputs, emptyList()))

fun ProgramState.resume(inputs: List<Int>) =
    evalProgram(copy(inputs = this.inputs + inputs, status = ProgramStatus.Running))

private tailrec fun evalProgram(state: ProgramState): ProgramState {
    if (state.status != ProgramStatus.Running) {
        return state
    }

    val operator = OPERATORS.firstOrNull { it.opCode == state.opCode }
        ?: throw UnknownOperatorException(state.opCode, state.pc)
    return evalProgram(operator(state))
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
    Operator(3, 1) { state ->
        val newState = state.readValue(state.op(1))
        Pair(newState, newState.status == ProgramStatus.Running)
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
    val status: ProgramStatus = ProgramStatus.Running
) {

    val opCode: Int
        get() = memory[pc] % 100

    fun updateMemory(address: Int, value: Int) = copy(memory = memory.withUpdatedValue(address, value))
    fun terminate() = copy(status = ProgramStatus.Terminated)
    fun suspend() = copy(status = ProgramStatus.Suspended)
    fun advancePc(steps: Int) = copy(pc = pc + steps)
    fun setPc(address: Int) = copy(pc = address)
    fun readValue(targetAddress: Int): ProgramState {
        if (inputs.isEmpty()) {
            return suspend()
        }
        val input = inputs.first()
        return copy(memory = memory.withUpdatedValue(targetAddress, input), inputs = inputs.drop(1))
    }

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

enum class ProgramStatus {
    Running, Suspended, Terminated
}
