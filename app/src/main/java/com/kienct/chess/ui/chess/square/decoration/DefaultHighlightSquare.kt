package com.kienct.chess.ui.chess.square.decoration

import androidx.compose.ui.graphics.Color

/**
 * A default implementation of [HighlightSquare] with a yellow color and low alpha.
 *
 * This object represents a pre-configured highlight square often used for visual cues or
 * selections in UI elements. It has a semi-transparent yellow appearance.
 *
 * Properties:
 * - `color`: The color of the highlight square, set to [Color.Yellow].
 * - `alpha`: The transparency of the highlight square, set to 0.15f (15% opacity).
 *
 */
object DefaultHighlightSquare : HighlightSquare(
    color = Color.Yellow,
    alpha = 0.15f
)