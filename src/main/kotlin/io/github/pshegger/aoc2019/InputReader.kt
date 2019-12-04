package io.github.pshegger.aoc2019

import java.io.File

object InputReader {

    fun <T> read(fileName: String, block: File.() -> T) =
        with(File("inputs/$fileName"), block)
}
