package com.kienct.chess.ui.chess.board.decoration

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import com.kienct.chess.model.board.Position
import com.kienct.chess.ui.chess.board.BoardDecoration
import com.kienct.chess.ui.chess.board.BoardRenderProperties
import com.kienct.chess.ui.chess.square.SquareDecoration
import com.kienct.chess.ui.chess.square.SquareRenderProperties
import com.kienct.chess.ui.chess.toOffsetModifier
import java.util.UUID

/**
 * `DecoratedSquares` is a `BoardDecoration` implementation responsible for rendering
 * individual squares on a game board, along with any associated decorations.
 *
 * This class iterates over all possible positions on the board and renders a `Square`
 * for each. Each square's appearance and behavior (e.g., highlighting, click ability)
 * are determined by the provided `BoardRenderProperties` and the specific position
 * of the square. Decorations, if any, are applied to each square.
 *
 * @property decorations A list of `SquareDecoration` instances that will be applied to each square.
 *   These decorations can add visual elements or effects to the squares.
 *
 * @see BoardDecoration
 * @see SquareDecoration
 * @see BoardRenderProperties
 * @see SquareRenderProperties
 */
class DecoratedSquares(private val decorations: List<SquareDecoration>) : BoardDecoration {
    /**
     * Renders the board based on the provided [BoardRenderProperties].
     *
     * This function iterates through all possible positions on the board and renders a [Square] for each.
     * It determines the properties of each square based on the current [BoardRenderProperties] and the
     * specific position. These properties include highlighting, click ability, whether it's a possible
     * move or capture, and the action to perform when the square is clicked.
     *
     * @param properties The properties that define how the board should be rendered. This includes:
     *                   - uiState: Represents the current state of the board UI, including:
     *                     - highlightedPositions: A set of positions that should be visually highlighted.
     *                     - clickablePositions: A set of positions that should be clickable.
     *                     - possibleMovesWithoutCaptures: A set of positions representing possible moves without captures.
     *                     - possibleCaptures: A set of positions representing possible captures.
     *                   - onClick: A lambda function that is invoked when a square is clicked. It receives the clicked position as a parameter.
     *                   - any other properties required for rendering the board.
     *
     * @see Square
     * @see BoardRenderProperties
     * @see SquareRenderProperties
     * @see Position
     */
    @Composable
    override fun render(properties: BoardRenderProperties) {
        Position.entries.forEach { position ->
            key(position) {
                val squareProperties = remember(properties) {
                    SquareRenderProperties(
                        position = position,
                        isHighlighted = position in properties.uiState.highlightedPositions,
                        clickable = position in properties.uiState.clickablePositions,
                        isPossibleMoveWithoutCapture = position in properties.uiState.possibleMovesWithoutCaptures,
                        isPossibleCapture = position in properties.uiState.possibleCaptures,
                        onClick = { properties.onClick(position) },
                        boardProperties = properties
                    )
                }
                Square(
                    properties = squareProperties,
                    decorations = decorations
                )
            }
        }
    }

    /**
     * A composable function that renders a square on the board.
     *
     * This function represents a single square on a game board. It handles the
     * rendering of the square based on its properties and any decorations
     * that need to be applied to it. It also manages click events on the square.
     *
     * @param properties The [SquareRenderProperties] that define the square's
     *   position, size, click ability, and other visual attributes.
     * @param decorations A list of [SquareDecoration] objects. Each decoration
     *   is applied to the square and can add visual elements or effects.
     *
     * @see SquareRenderProperties
     * @see SquareDecoration
     */
    @Composable
    private fun Square(properties: SquareRenderProperties, decorations: List<SquareDecoration>) {
        Box(
            modifier = properties.coordinate
                .toOffsetModifier(properties.boardProperties.squareSize)
                .pointerInput(UUID.randomUUID()) {
                    detectTapGestures(
                        onPress = { if (properties.clickable) properties.onClick() },
                    )
                }
        ) {
            decorations.forEach {
                it.render(properties)
            }
        }
    }

}