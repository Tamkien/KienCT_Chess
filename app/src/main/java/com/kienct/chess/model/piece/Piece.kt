package com.kienct.chess.model.piece

import android.os.Parcelable
import com.kienct.chess.controller.state.GameSnapshotState
import com.kienct.chess.model.move.BoardMove

interface Piece : Parcelable {
    val color: Color
    val asset: Int?
    val symbol: String
    val textSymbol: String

    /**
     * Calculates and returns a list of pseudo-legal moves for the current player
     * in the given game snapshot state.
     *
     * Pseudo-legal moves are moves that are legal according to the rules of chess,
     * except that they may leave the player's own king in check.
     *
     * @param gameSnapshotState The current state of the game.
     * @param isChecked A flag indicating whether the current player's king is in check.
     * @return A list of BoardMove objects representing the pseudo-legal moves.
     *         Returns an empty list if no pseudo-legal moves are found.
     */
    fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, isChecked: Boolean = false): List<BoardMove> = emptyList()
}