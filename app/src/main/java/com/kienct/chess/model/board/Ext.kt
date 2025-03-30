package com.kienct.chess.model.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp

/**
 * Calculates the index of a square on a chessboard given its file and rank.
 *
 * The chessboard is represented as a 1D array of 64 elements, where the index
 * starts at 0 for the square a1 (file 1, rank 1) and increases sequentially
 * across files and then ranks.  For example, b1 would be index 1, a2 would be index 8,
 * and h8 would be index 63.
 *
 * @param file The file (column) of the square, ranging from 1 (a-file) to 8 (h-file).
 * @param rank The rank (row) of the square, ranging from 1 (first rank) to 8 (eighth rank).
 * @return The index of the square in the 1D representation, ranging from 0 to 63.
 * @throws IllegalArgumentException if either `file` or `rank` are outside the valid range [1, 8].
 */
fun idx(file: Int, rank: Int): Int =
    (file - 1) * 8 + (rank - 1)

/**
 * Validates if the given file and rank represent a valid square on a chessboard.
 *
 * A valid square must have a file and rank within the range of 1 to 8, inclusive.
 *
 * @param file The file (column) of the square, ranging from 1 to 8.
 * @param rank The rank (row) of the square, ranging from 1 to 8.
 * @throws IllegalArgumentException if either the file or rank is outside the valid range.
 */
fun validate(file: Int, rank: Int) {
    require(file >= 1)
    require(file <= 8)
    require(rank >= 1)
    require(rank <= 8)
}

/**
 * Checks if a [Position] represents a dark square on a chessboard.
 *
 * A chessboard consists of alternating dark and light squares, starting with a light square
 * in the top-left corner (A8). This function determines if a given [Position] corresponds
 * to a dark square based on its rank and file.
 *
 * @return `true` if the [Position] represents a dark square, `false` otherwise.
 */
fun Position.isDarkSquare(): Boolean =
    (ordinal + file % 2) % 2 == 1

/**
 * Converts a [Position] on a chessboard to a [Coordinate] on a UI canvas.
 *
 * The conversion takes into account whether the board is flipped, which affects
 * the mapping of files and ranks to x and y coordinates.  A flipped board reverses
 * the x-axis (files).  The origin (0,0) of the Coordinate system is at the top-left
 * of the board, with the x-axis increasing to the right and the y-axis increasing downwards.
 *
 * @param isFlipped A boolean indicating whether the board is flipped. If `true`, the files
 *                  are reversed along the x-axis.
 * @return A [Coordinate] representing the UI position of the given board position.
 */
fun Position.toCoordinate(isFlipped: Boolean): Coordinate = if (isFlipped)
    Coordinate(
        x = Coordinate.max.x - file + 1,
        y = rank.toFloat(),
    ) else Coordinate(
    x = file.toFloat(),
    y = Coordinate.max.y - rank + 1,
)

/**
 * Converts a [Coordinate] representing a grid position to an [Offset] in pixels, based on a given square size.
 *
 * The coordinate system used is such that (1, 1) represents the top-left corner of the grid, and the
 * returned offset is the top-left corner of the square at that coordinate.
 *
 * @param squareSize The size of each square in the grid in Dp units.
 * @return An [Offset] representing the top-left corner of the square at the given [Coordinate], in pixels.
 */
fun Coordinate.toOffset(squareSize: Dp): Offset =
    Offset(
        x = (x - 1) * squareSize.value,
        y = (y - 1) * squareSize.value
    )