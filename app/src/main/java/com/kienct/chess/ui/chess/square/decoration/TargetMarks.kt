package com.kienct.chess.ui.chess.square.decoration

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.kienct.chess.ui.chess.square.SquareDecoration
import com.kienct.chess.ui.chess.square.SquareRenderProperties
import java.util.UUID

object TargetMarks : SquareDecoration {
    /**
     * Renders a square based on its properties, determining whether it should be
     * displayed as a possible move without capture or a possible capture.
     *
     * @param properties The properties of the square to be rendered, including
     *                   whether it's a possible move without capture, a possible
     *                   capture, the click action, and the size modifier.
     *                   - `isPossibleMoveWithoutCapture`: Indicates if the square
     *                     represents a possible move without capturing another piece.
     *                   - `isPossibleCapture`: Indicates if the square represents a
     *                     possible capture of another piece.
     *                   - `onClick`: The action to be performed when the square is
     *                     clicked.
     *                   - `sizeModifier`: The modifier to be applied to the size of the
     *                      square.
     *
     * If `isPossibleMoveWithoutCapture` is true, it renders a `PossibleMoveWithoutCapture`
     * composable. If `isPossibleCapture` is true, it renders a `PossibleCapture`
     * composable. If neither is true, nothing is rendered.
     *
     * Note: It's expected that only one of `isPossibleMoveWithoutCapture` or
     * `isPossibleCapture` is true at a time. If both are true, the code will
     * prioritize rendering the `PossibleMoveWithoutCapture`. if both are false nothing is rendered.
     */
    @Composable
    override fun render(properties: SquareRenderProperties) {
        if (properties.isPossibleMoveWithoutCapture) {
            PossibleMoveWithoutCapture(
                onClick = properties.onClick,
                modifier = properties.sizeModifier
            )
        } else if (properties.isPossibleCapture) {
            PossibleCapture(
                onClick = properties.onClick,
                modifier = properties.sizeModifier
            )
        }
    }

    @Composable
    private fun PossibleCapture(onClick: () -> Unit, modifier: Modifier) {
        CircleDecoratedSquare(
            onClick = onClick,
            radius = { size.minDimension / 3f },
            drawStyle = { Stroke(width = size.minDimension / 12f) },
            modifier = modifier
        )
    }

    @Composable
    private fun PossibleMoveWithoutCapture(onClick: () -> Unit, modifier: Modifier) {
        CircleDecoratedSquare(
            onClick = onClick,
            radius = { size.minDimension / 6f },
            drawStyle = { Fill },
            modifier = modifier
        )
    }

    @Composable
    private fun CircleDecoratedSquare(
        onClick: () -> Unit,
        radius: DrawScope.() -> Float,
        drawStyle: DrawScope.() -> DrawStyle,
        modifier: Modifier
    ) {
        Canvas(
            modifier = modifier
                .pointerInput(UUID.randomUUID()) {
                    detectTapGestures(
                        onPress = { onClick() },
                    )
                }
        ) {
            drawCircle(
                color = Color.DarkGray,
                radius = radius(this),
                alpha = 0.25f,
                style = drawStyle(this)
            )
        }
    }
}