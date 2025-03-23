package com.kienct.chess.ui.chess.pieces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.kienct.chess.model.piece.Piece

/**
 * Composable function to display a chess piece on the board.
 *
 * This function renders a single chess piece, either as an image icon or a text symbol,
 * within a square area of the specified size. It uses the provided [Piece] data class
 * to determine the piece's color, type, and representation.
 *
 * @param piece The [Piece] data class representing the chess piece to display.
 * @param squareSize The size (width and height) of the square area in which the piece will be displayed.
 * @param modifier Modifier to be applied to the piece's layout.
 */
@Composable
fun Piece(
    piece: Piece,
    squareSize: Dp,
    modifier: Modifier = Modifier
) {
    key(piece) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.size(squareSize, squareSize)
        ) {
            piece.asset?.let {
                Icon(
                    painter = painterResource(id = it),
                    tint = Color.Unspecified,
                    contentDescription = "${piece.color} ${piece.javaClass.simpleName}"
                )
            } ?: run {
                Text(
                    text = piece.symbol,
                    color = Color.Black,
                    fontSize = with(LocalDensity.current) {
                        (squareSize / 5 * 4).toSp()
                    }
                )
            }
        }
    }
}
