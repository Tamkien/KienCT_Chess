package com.kienct.chess.model.piece

import com.kienct.chess.R
import com.kienct.chess.model.game.state.GameSnapshotState
import com.kienct.chess.model.move.BoardMove
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class Bishop(override val color: Color) : Piece {
    @IgnoredOnParcel
    override val asset: Int =
        if (this.isWhite()) R.drawable.bishop_light else R.drawable.bishop_dark

    @IgnoredOnParcel
    override val symbol: String = if (this.isWhite()) "♗" else "♝"

    @IgnoredOnParcel
    override val textSymbol: String = SYMBOL

    override fun pseudoLegalMoves(
        gameSnapshotState: GameSnapshotState,
        isChecked: Boolean
    ): List<BoardMove> =
        lineMoves(gameSnapshotState, directions)

    companion object {
        val directions = listOf(
            -1 to -1,
            -1 to 1,
            1 to -1,
            1 to 1,
        )

        const val SYMBOL: String = "B"
    }
}