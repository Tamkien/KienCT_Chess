package com.kienct.chess.model.game.state

import android.os.Parcelable
import com.kienct.chess.model.board.Board
import com.kienct.chess.model.board.Position
import com.kienct.chess.model.board.Position.a1
import com.kienct.chess.model.board.Position.a8
import com.kienct.chess.model.board.Position.e1
import com.kienct.chess.model.board.Position.e8
import com.kienct.chess.model.board.Position.h1
import com.kienct.chess.model.board.Position.h8
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.piece.Color
import com.kienct.chess.model.piece.Color.BLACK
import com.kienct.chess.model.piece.Color.WHITE
import com.kienct.chess.model.piece.King
import com.kienct.chess.model.piece.Rook
import kotlinx.parcelize.Parcelize

@Parcelize
data class CastlingInfo(
    val holders: Map<Color, Holder> = mapOf(
        WHITE to Holder(),
        BLACK to Holder()
    )
) : Parcelable {
    @Parcelize
    data class Holder(
        val kingHasMoved: Boolean = false,
        val kingSideRookHasMoved: Boolean = false,
        val queenSideRookHasMoved: Boolean = false,
    ) : Parcelable {

        val canCastleKingSide: Boolean
            get() = !kingHasMoved && !kingSideRookHasMoved

        val canCastleQueenSide: Boolean
            get() = !kingHasMoved && !queenSideRookHasMoved
    }

    operator fun get(color: Color) = holders[color]!!

    /**
     * Applies a move to the castling information, updating whether the king or rooks have moved.
     *
     * This function takes a [BoardMove] as input, which represents a move made on the chessboard.
     * It then updates the castling information based on this move, specifically checking if the
     * king or either rook of the moving player has moved.
     *
     * @param boardMove The move that was made on the board, including the piece and the move details.
     * @return A new [CastlingInfo] object with updated information about whether the king and rooks have moved.
     * @throws IllegalArgumentException if the color of the moving piece is not present in the holders map.
     *
     */
    fun apply(boardMove: BoardMove): CastlingInfo {
        val move = boardMove.move
        val piece = boardMove.piece
        val color = piece.color
        val holder = holders[color]!!

        val kingSideRookInitialPosition = if (color == WHITE) Position.h1 else Position.h8
        val queenSideRookInitialPosition = if (color == WHITE) Position.a1 else Position.a8

        val updatedHolder = holder.copy(
            kingHasMoved = holder.kingHasMoved || piece is King,
            kingSideRookHasMoved = holder.kingSideRookHasMoved || piece is Rook && move.from == kingSideRookInitialPosition,
            queenSideRookHasMoved = holder.queenSideRookHasMoved || piece is Rook && move.from == queenSideRookInitialPosition,
        )

        return copy(
            holders = holders
                .minus(color)
                .plus(color to updatedHolder)
        )
    }

    companion object {
        /**
         * Creates a [CastlingInfo] object from a given [Board].
         *
         * This function determines the castling rights for both white and black based on the initial
         * positions of the kings and rooks on the board. It checks if the kings and the specific
         * rooks (king-side and queen-side) have moved from their starting positions.
         *
         * @param board The [Board] object representing the current state of the chessboard.
         * @return A [CastlingInfo] object containing the castling availability information for both
         *         white and black players.
         *         - `kingHasMoved`: Indicates if the king has moved from its initial square (e1 for white, e8 for black).
         *         - `kingSideRookHasMoved`: Indicates if the king-side rook has moved from its initial square (h1 for white, h8 for black).
         *         - `queenSideRookHasMoved`: Indicates if the queen-side rook has moved from its initial square (a1 for white, a8 for black).
         */
        fun from(board: Board): CastlingInfo {
            val whitePieces = board.pieces(WHITE)
            val whiteHolder = Holder(
                kingHasMoved = whitePieces[e1] !is King,
                kingSideRookHasMoved = whitePieces[h1] !is Rook,
                queenSideRookHasMoved = whitePieces[a1] !is Rook,
            )
            val blackPieces = board.pieces(BLACK)
            val blackHolder = Holder(
                kingHasMoved = blackPieces[e8] !is King,
                kingSideRookHasMoved = blackPieces[h8] !is Rook,
                queenSideRookHasMoved = blackPieces[a8] !is Rook,
            )

            return CastlingInfo(
                mapOf(
                    WHITE to whiteHolder,
                    BLACK to blackHolder
                )
            )
        }
    }

}