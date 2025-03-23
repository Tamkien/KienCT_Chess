package com.kienct.chess.model.move

import android.os.Parcelable
import com.kienct.chess.model.board.Position
import com.kienct.chess.model.piece.Piece
import com.kienct.chess.model.piece.isWhite
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppliedMove(
    val boardMove: BoardMove,
    val effect: MoveEffect? = null
) : Parcelable {

    @IgnoredOnParcel
    val move: PrimaryMove = boardMove.move

    @IgnoredOnParcel
    val from: Position = move.from

    @IgnoredOnParcel
    val to: Position = move.to

    @IgnoredOnParcel
    val piece: Piece = move.piece

    override fun toString(): String {
        val postFix = when (effect) {
            MoveEffect.CHECK -> "+"
            MoveEffect.CHECKMATE -> "#  ${if (boardMove.move.piece.isWhite()) "1-0" else "0-1"}"
            MoveEffect.DRAW -> "  ½ - ½"
            else -> ""
        }
        return "$boardMove$postFix"
    }
}
