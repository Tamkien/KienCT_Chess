package com.kienct.chess.ui.chess.square.decoration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kienct.chess.ui.chess.square.SquareDecoration
import com.kienct.chess.ui.chess.square.SquareRenderProperties

open class HighlightSquare(
    private val color: Color,
    private val alpha: Float
) : SquareDecoration {

    /**
     * Data class holding the properties needed to render a square.
     *
     * @property isHighlighted True if the square should be highlighted, false otherwise.
     * @property sizeModifier Modifier to define the size of the square.
     */
    @Composable
    override fun render(properties: SquareRenderProperties) {
        if (properties.isHighlighted) {
            Canvas(properties.sizeModifier) {
                drawRect(
                    color = color,
                    size = size,
                    alpha = alpha
                )
            }
        }
    }
}