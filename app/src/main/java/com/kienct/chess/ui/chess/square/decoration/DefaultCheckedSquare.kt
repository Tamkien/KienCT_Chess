package com.kienct.chess.ui.chess.square.decoration

import androidx.compose.ui.graphics.Color

/**
 * A default [CheckedSquare] object with a red color and an alpha value of 0.15f.
 *
 * This provides a readily available instance of a checked square with predefined styling.
 * It's used to visually highlight the checked state of a king piece in chess.
 */
object DefaultCheckedSquare : CheckedSquare(
    color = Color.Red,
    alpha = 0.25f
)