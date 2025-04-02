package com.kienct.chess.model.piece

import com.kienct.chess.R
import com.kienct.chess.controller.state.GameSnapshotState
import com.kienct.chess.model.move.BoardMove
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class Knight(override val color: Color) : Piece {
    @IgnoredOnParcel
    override val asset: Int =
        if (this.isWhite()) R.drawable.knight_light else R.drawable.knight_dark

    @IgnoredOnParcel
    override val symbol: String = if (this.isWhite()) "♘" else "♞"

    @IgnoredOnParcel
    override val textSymbol: String = SYMBOL

    override fun pseudoLegalMoves(
        gameSnapshotState: GameSnapshotState,
        isChecked: Boolean
    ): List<BoardMove> =
        targets.mapNotNull { singleCaptureMove(gameSnapshotState, it.first, it.second) }

    companion object {
        const val SYMBOL: String = "N"
        val targets = listOf(
            -2 to 1,
            -2 to -1,
            2 to 1,
            2 to -1,
            1 to 2,
            1 to -2,
            -1 to 2,
            -1 to -2
        )
    }
}