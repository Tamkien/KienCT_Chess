package com.kienct.chess.ui.chess.square.decoration

import com.kienct.chess.ui.base.square_dark
import com.kienct.chess.ui.base.square_light

/**
 * Represents the default square background configuration used in a chess board or similar grid-based layout.
 *
 * This object provides a predefined `SquareBackground` with light and dark square colors.
 * The light squares are assigned the color `square_light` and dark squares are assigned the color `square_dark`.
 *
 * @see SquareBackground
 *
 * @property lightSquareColor The color of the light squares.
 * @property darkSquareColor The color of the dark squares.
 */
object DefaultSquareBackground: SquareBackground(
    lightSquareColor = square_light,
    darkSquareColor = square_dark
)