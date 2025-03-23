package com.kienct.chess.ui.chess

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.kienct.chess.model.board.Coordinate

fun Coordinate.toOffsetModifier(squareSize: Dp): Modifier =
    Modifier.offset(
            Dp((x - 1) * squareSize.value),
            Dp((y - 1) * squareSize.value)
        )

fun Offset.toModifier(): Modifier =
    Modifier.offset(Dp(x), Dp(y))