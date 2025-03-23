package com.kienct.chess.model.board

import com.kienct.chess.model.piece.Color
import com.kienct.chess.model.piece.Piece
import com.kienct.chess.model.piece.isBlack
import com.kienct.chess.model.piece.isWhite

class Square (
    val position: Position,
    val piece: Piece? = null
) {

    val file: Int =
        position.file

    val rank: Int =
        position.rank

    val isDark: Boolean =
        position.isDarkSquare()

    val isEmpty: Boolean
        get() = piece == null

    val isNotEmpty: Boolean
        get() = !isEmpty

    fun hasPiece(color: Color): Boolean =
        piece?.color == color

    val hasWhitePiece: Boolean
        get() = piece?.isWhite() == true

    val hasBlackPiece: Boolean
        get() = piece?.isBlack() == true

    override fun toString(): String =
        File.entries[file - 1].toString() + rank.toString()
}
