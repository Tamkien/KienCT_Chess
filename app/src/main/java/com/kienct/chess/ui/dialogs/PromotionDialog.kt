package com.kienct.chess.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kienct.chess.model.piece.Bishop
import com.kienct.chess.model.piece.Color
import com.kienct.chess.model.piece.Knight
import com.kienct.chess.model.piece.Piece
import com.kienct.chess.model.piece.Queen
import com.kienct.chess.model.piece.Rook
import com.kienct.chess.ui.chess.pieces.Piece

/**
 * Displays a dialog for promoting a pawn to a different piece.
 *
 * This composable function presents a modal dialog that allows the user to
 * select a piece (Queen, Rook, Bishop, or Knight) to which they want to
 * promote a pawn. The dialog does not dismiss until a piece is selected.
 *
 * @param color The background color of the dialog. Defaults to `Color.WHITE`.
 * @param onPieceSelected A callback function that is invoked when the user
 *                        selects a piece. It receives the selected `Piece` as a parameter.
 *
 * @see PromotionDialogContent
 * @see Piece
 */
@Composable
fun PromotionDialog(
    color: Color = Color.WHITE,
    onPieceSelected: (Piece) -> Unit,
) {
    MaterialTheme {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            PromotionDialogContent(color) {
                onPieceSelected(it)
            }
        }
    }
}

/**
 * Composable function that displays a dialog content for piece promotion in a chess game.
 *
 * This function presents a vertical list of available promotion pieces (Queen, Rook, Bishop, Knight)
 * to the user. Each piece is displayed as a clickable icon.
 *
 * @param color The color of the pieces to be displayed. This typically represents the
 *              player's color (e.g., white or black). Defaults to [Color.WHITE].
 * @param onClick A lambda function that is invoked when a piece is clicked.
 *                It receives the selected [Piece] as a parameter. Defaults to an empty lambda.
 *
 * @sample PromotionDialogContent
 */
@Preview
@Composable
private fun PromotionDialogContent(
    color: Color = Color.WHITE,
    onClick: (Piece) -> Unit = {}
) {
    val promotionPieces = listOf(
        Queen(color),
        Rook(color),
        Bishop(color),
        Knight(color)
    )

    Column(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        promotionPieces.forEach { piece ->
            Piece(
                piece = piece,
                squareSize = 48.dp,
                modifier = Modifier.clickable(onClick = { onClick(piece) })
            )
        }
    }
}
