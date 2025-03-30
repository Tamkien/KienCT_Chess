package com.kienct.chess.ui.chess.square

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import com.kienct.chess.model.board.Coordinate
import com.kienct.chess.model.board.Position
import com.kienct.chess.model.board.toCoordinate
import com.kienct.chess.ui.chess.board.BoardRenderProperties

/**
 * Represents the rendering properties of a single square on the chessboard.
 *
 * This data class encapsulates all the information needed to visually represent
 * a square on the board, including its position, highlighting state, interactivity,
 * and other visual attributes.
 *
 * @property position The logical position of the square on the board (e.g., a1, h8).
 * @property isHighlighted Indicates whether the square should be visually highlighted.
 * @property clickable Indicates whether the square is interactive and can be clicked.
 * @property onClick The action to be executed when the square is clicked.
 * @property isPossibleMoveWithoutCapture Indicates whether a piece can move to this square without capturing an opponent's piece.
 * @property isPossibleCapture Indicates whether a piece can move to this square and capture an opponent's piece.
 * @property boardProperties The rendering properties of the entire chessboard, such as square size and flipped state.
 */
data class SquareRenderProperties(
    val position: Position,
    val checkedKingPosition: Position?,
    val isHighlighted: Boolean,
    val clickable: Boolean,
    val onClick: () -> Unit,
    val isPossibleMoveWithoutCapture: Boolean,
    val isPossibleCapture: Boolean,
    val boardProperties: BoardRenderProperties
) {
    val coordinate: Coordinate =
        position.toCoordinate(boardProperties.isFlipped)

    val sizeModifier: Modifier
        get() = Modifier.size(boardProperties.squareSize)
}