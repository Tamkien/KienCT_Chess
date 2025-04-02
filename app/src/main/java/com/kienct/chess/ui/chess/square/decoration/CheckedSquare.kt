package com.kienct.chess.ui.chess.square.decoration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kienct.chess.ui.chess.square.SquareDecoration
import com.kienct.chess.ui.chess.square.SquareRenderProperties

/**
 * A [SquareDecoration] that renders a colored overlay on a square if the provided position
 * matches the currently checked king's position.  This is used to visually indicate that a king is in check.
 *
 * @property color The color of the overlay to draw.
 * @property alpha The transparency of the overlay, ranging from 0.0 (fully transparent) to 1.0 (fully opaque).
 */
open class CheckedSquare(
    private val color: Color,
    private val alpha: Float
): SquareDecoration {
    /**
     * Renders a visual indicator (a rectangle) on a chess square if the square corresponds to the checked king's position.
     *
     * @param properties [SquareRenderProperties] containing the properties needed for rendering the square.
     *  This includes information such as the square's position, the checked king's position, and a size modifier for the canvas.
     *
     * The visual indicator is drawn only when the `checkedKingPosition` within the `properties` matches the `position` of the square.
     * The indicator is a rectangle with customizable `color` and `alpha` (transparency).  If the square is *not* the checked king's position, nothing is rendered.
     */
    @Composable
    override fun render(properties: SquareRenderProperties) {
        if (properties.checkedKingPosition == properties.position) {
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
