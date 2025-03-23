package com.kienct.chess.model.move

import android.os.Parcelable
import com.kienct.chess.model.board.Position
import com.kienct.chess.model.piece.Pawn
import com.kienct.chess.model.piece.Piece
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.EnumSet

@Parcelize
data class BoardMove(
    val move: PrimaryMove,
    val preMove: PreMove? = null,
    val consequence: Consequence? = null,
    val ambiguity: EnumSet<Ambiguity> = EnumSet.noneOf(Ambiguity::class.java)
) : Parcelable {


    @IgnoredOnParcel
    val from: Position = move.from

    @IgnoredOnParcel
    val to: Position = move.to

    @IgnoredOnParcel
    val piece: Piece = move.piece

    enum class Ambiguity {
        AMBIGUOUS_FILE, AMBIGUOUS_RANK,
    }
    override fun toString(): String {
        if (move is KingSideCastle) return "O-O"
        if (move is QueenSideCastle) return "O-O-O"
        val isCapture = preMove is Capture
        val symbol = when {
            piece !is Pawn -> piece.textSymbol
            isCapture -> from.fileAsLetter
            else -> ""
        }
        val file = if (ambiguity.contains(Ambiguity.AMBIGUOUS_FILE)) from.fileAsLetter.toString() else ""
        val rank = if (ambiguity.contains(Ambiguity.AMBIGUOUS_RANK)) from.rank.toString() else ""
        val capture = if (isCapture) "x" else ""
        val promotion = if (consequence is Promotion) "=${consequence.piece.textSymbol}" else ""

        return "$symbol$file$rank$capture$to$promotion"
    }
}