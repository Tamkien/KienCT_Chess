package com.kienct.chess.model.board

data class Coordinate(
    val x: Float,
    val y: Float,
) {
    operator fun plus(other: Coordinate): Coordinate =
        Coordinate(x + other.x, y + other.y)

    operator fun minus(other: Coordinate): Coordinate =
        Coordinate(x - other.x, y - other.y)

    operator fun times(factor: Float): Coordinate =
        Coordinate(x * factor, y * factor)

    companion object {
        val min = Coordinate(1f, 1f)
        val max = Coordinate(8f, 8f)
    }
}
