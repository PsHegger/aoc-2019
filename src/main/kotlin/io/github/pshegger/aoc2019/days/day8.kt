package io.github.pshegger.aoc2019.days

import io.github.pshegger.aoc2019.Answer
import io.github.pshegger.aoc2019.InputReader

private const val WIDTH = 25
private const val HEIGHT = 6
private const val LAYER_SIZE = WIDTH * HEIGHT

private const val COLOR_BLACK = 0
private const val COLOR_WHITE = 1
private const val COLOR_TRANSPARENT = 2

fun day8(): Answer {
    val image = InputReader.read("day8.txt") {
        val pixels = readLines().flatMap { line -> line.toList().map { "$it".toInt() } }
        pixels.chunked(LAYER_SIZE)
    }

    val fewest0Layer = image
        .mapIndexed { index, layer -> Pair(index, layer.count { it == 0 }) }
        .minBy { it.second }
        ?.first ?: throw MinNotFoundError
    val digits1 = image[fewest0Layer].count { it == 1 }
    val digits2 = image[fewest0Layer].count { it == 2 }

    return Answer.Full(digits1 * digits2, "\n${renderImage(image)}")
}

private fun renderImage(image: List<List<Int>>): String =
    image.fold(List(LAYER_SIZE) { COLOR_TRANSPARENT }) { rendered, layer ->
        rendered.mapIndexed { pixelIndex, color ->
            val layerColor = layer[pixelIndex]
            if (color == COLOR_TRANSPARENT) {
                layerColor
            } else {
                color
            }
        }
    }
        .chunked(WIDTH)
        .joinToString(separator = "\n") { line ->
            line.joinToString(separator = "") { color ->
                when (color) {
                    COLOR_BLACK -> " "
                    COLOR_WHITE -> "#"
                    else -> "_"
                }
            }
        }

object MinNotFoundError : Throwable()
