package com.kienct.chess.ui.chess.square.decoration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.kienct.chess.model.board.Coordinate
import com.kienct.chess.ui.chess.square.SquareDecoration
import com.kienct.chess.ui.chess.square.SquareRenderProperties

open class SquarePositionLabel(
    private val displayFile: (Coordinate) -> Boolean,
    private val displayRank: (Coordinate) -> Boolean,
) : SquareDecoration {

    /**
     * Renders the position labels (file and rank) on a chess square if they should be displayed.
     *
     * This function is responsible for drawing the file (letter) and rank (number)
     * indicators on a square of the chessboard, based on the provided properties.
     *
     * @param properties The properties that define how the square should be rendered,
     *                   including its coordinate, size, and position.
     *                   - `coordinate`: The coordinate of the square on the chessboard (e.g., a1, h8).
     *                   - `position`:  The position object containing the file and rank information.
     *                   - `sizeModifier`: The modifier to apply to the position label (e.g., for size and padding).
     * @see SquareRenderProperties
     * @see PositionLabel
     * @see displayFile
     * @see displayRank
     */
    @Composable
    override fun render(properties: SquareRenderProperties) {
        if (displayFile(properties.coordinate))
            PositionLabel(
                text = properties.position.fileAsLetter.toString(),
                alignment = Alignment.BottomEnd,
                modifier = properties.sizeModifier
            )

        if (displayRank(properties.coordinate))
            PositionLabel(
                text = properties.position.rank.toString(),
                alignment = Alignment.TopStart,
                modifier = properties.sizeModifier
            )
    }
}
