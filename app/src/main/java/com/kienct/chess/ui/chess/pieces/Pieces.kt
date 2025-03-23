package com.kienct.chess.ui.chess.pieces

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.kienct.chess.model.board.toCoordinate
import com.kienct.chess.model.board.toOffset
import com.kienct.chess.ui.chess.board.BoardDecoration
import com.kienct.chess.ui.chess.board.BoardRenderProperties
import com.kienct.chess.ui.chess.toModifier

object Pieces : BoardDecoration {

    /**
     * Renders the chess pieces on the board based on the provided [BoardRenderProperties].
     *
     * This function iterates through the pieces in the `toState` of the board,
     * calculates their initial and target positions, and animates them to their
     * new locations if they have moved. It also handles board flipping.
     *
     * @param properties [BoardRenderProperties] containing information about the current
     *   and previous board states, square size, and whether the board is flipped.
     *
     *   - `properties.toState`: The target state of the board.
     *   - `properties.fromState`: The previous state of the board. Used for determining piece movement.
     *   - `properties.squareSize`: The size of each square on the board.
     *   - `properties.isFlipped`: Indicates whether the board is flipped.
     *
     */
    @Composable
    override fun render(properties: BoardRenderProperties) {
        properties.toState.board.pieces.forEach { (toPosition, piece) ->
            key(piece) {
                val fromPosition = properties.fromState.board.find(piece)?.position
                val currentOffset = fromPosition
                    ?.toCoordinate(properties.isFlipped)
                    ?.toOffset(properties.squareSize)

                val targetOffset = toPosition
                    .toCoordinate(properties.isFlipped)
                    .toOffset(properties.squareSize)

                val offset = remember { Animatable(currentOffset ?: targetOffset, Offset.VectorConverter) }
                LaunchedEffect(targetOffset) {
                    offset.animateTo(targetOffset, tween(100, easing = LinearEasing))
                }
                LaunchedEffect(properties.isFlipped) {
                    offset.snapTo(targetOffset)
                }

                Piece(
                    piece = piece,
                    squareSize = properties.squareSize,
                    modifier = offset.value.toModifier()
                )
            }
        }
    }

}