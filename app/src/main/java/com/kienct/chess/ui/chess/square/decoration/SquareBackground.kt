package com.kienct.chess.ui.chess.square.decoration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kienct.chess.model.board.isDarkSquare
import com.kienct.chess.ui.chess.square.SquareDecoration
import com.kienct.chess.ui.chess.square.SquareRenderProperties

open class SquareBackground(
    private val lightSquareColor: Color,
    private val darkSquareColor: Color,
) : SquareDecoration {

    /**
     * Represents the properties needed to render a square on a chessboard.
     *
     * @property sizeModifier The modifier to control the size and layout of the square.
     * @property position The position of the square on the chessboard.
     */
    @Composable
    override fun render(properties: SquareRenderProperties) {
        Canvas(properties.sizeModifier) {
            drawRect(color = if (properties.position.isDarkSquare()) darkSquareColor else lightSquareColor)
        }
    }
}