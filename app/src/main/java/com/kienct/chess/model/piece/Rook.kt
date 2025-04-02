package com.kienct.chess.model.piece

import com.kienct.chess.R
import com.kienct.chess.controller.state.GameSnapshotState
import com.kienct.chess.model.move.BoardMove
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class Rook(override val color: Color) : Piece {
    @IgnoredOnParcel
    override val asset: Int = if (this.isWhite()) R.drawable.rook_light else R.drawable.rook_dark

    @IgnoredOnParcel
    override val symbol: String = if (this.isWhite()) "♖" else "♜"

    @IgnoredOnParcel
    override val textSymbol: String = SYMBOL

    override fun pseudoLegalMoves(
        gameSnapshotState: GameSnapshotState,
        isChecked: Boolean
    ): List<BoardMove> =
        lineMoves(gameSnapshotState, directions)

    companion object {
        const val SYMBOL: String = "R"
        val directions = listOf(
            -1 to 0,
            1 to 0,
            0 to -1,
            0 to 1,
        )
    }

}