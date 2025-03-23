package com.kienct.chess.model.piece

import com.kienct.chess.R
import com.kienct.chess.model.game.state.GameSnapshotState
import com.kienct.chess.model.move.BoardMove
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class Queen(override val color: Color) : Piece {
    @IgnoredOnParcel
    override val asset: Int = if (this.isWhite()) R.drawable.queen_light else R.drawable.queen_dark

    @IgnoredOnParcel
    override val symbol: String = if (this.isWhite()) "♕" else "︎♛"

    @IgnoredOnParcel
    override val textSymbol: String = SYMBOL

    override fun pseudoLegalMoves(
        gameSnapshotState: GameSnapshotState,
        isChecked: Boolean
    ): List<BoardMove> =
        lineMoves(gameSnapshotState, Bishop.directions + Rook.directions)

    companion object {
        const val SYMBOL: String = "Q"
    }
}