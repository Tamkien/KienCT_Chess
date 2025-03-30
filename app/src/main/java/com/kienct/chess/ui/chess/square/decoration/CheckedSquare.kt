package com.kienct.chess.ui.chess.square.decoration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kienct.chess.ui.chess.square.SquareDecoration
import com.kienct.chess.ui.chess.square.SquareRenderProperties

open class CheckedSquare(
    private val color: Color,
    private val alpha: Float
): SquareDecoration {
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
