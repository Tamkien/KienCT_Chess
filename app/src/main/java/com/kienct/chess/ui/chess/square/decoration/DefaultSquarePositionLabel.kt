package com.kienct.chess.ui.chess.square.decoration

import com.kienct.chess.model.board.Coordinate

/**
 * Represents the default strategy for labeling squares on a board.
 *
 * This object provides a default implementation for displaying file and rank labels on a board.
 * It labels the squares along the top edge of the board with their file (column) letters,
 * and the squares along the left edge of the board with their rank (row) numbers.
 *
 * The specific logic is as follows:
 *   - **File Labels (Columns):** A file label is displayed for a square if its y-coordinate (row)
 *     is equal to the maximum y-coordinate (top row) of the board.
 *   - **Rank Labels (Rows):** A rank label is displayed for a square if its x-coordinate (column)
 *     is equal to the minimum x-coordinate (leftmost column) of the board.
 *
 * This object is designed to be a singleton and is used as the default labeling behavior
 * when no other labeling strategy is specified.
 */
object DefaultSquarePositionLabel : SquarePositionLabel(
    displayFile = { coordinate -> coordinate.y == Coordinate.max.y },
    displayRank = { coordinate -> coordinate.x == Coordinate.min.x }
)