package com.kienct.chess.model.piece

import com.kienct.chess.R
import com.kienct.chess.model.board.File.a
import com.kienct.chess.model.board.File.b
import com.kienct.chess.model.board.File.c
import com.kienct.chess.model.board.File.d
import com.kienct.chess.model.board.File.e
import com.kienct.chess.model.board.File.f
import com.kienct.chess.model.board.File.g
import com.kienct.chess.model.board.File.h
import com.kienct.chess.controller.state.GameSnapshotState
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.move.KingSideCastle
import com.kienct.chess.model.move.Move
import com.kienct.chess.model.move.QueenSideCastle
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class King(override val color: Color) : Piece {
    @IgnoredOnParcel
    override val asset: Int = if (this.isWhite()) R.drawable.king_light else R.drawable.king_dark

    @IgnoredOnParcel
    override val symbol: String = if (this.isWhite()) "♔" else "♚"

    @IgnoredOnParcel
    override val textSymbol: String = SYMBOL

    override fun pseudoLegalMoves(
        gameSnapshotState: GameSnapshotState,
        isChecked: Boolean
    ): List<BoardMove> {
        val moves = targets
            .mapNotNull { singleCaptureMove(gameSnapshotState, it.first, it.second) }
            .toMutableList()

        if (!isChecked) {
            castleKingSide(gameSnapshotState)?.let { moves += it }
            castleQueenSide(gameSnapshotState)?.let { moves += it }
        }

        return moves
    }

    /**
     * Attempts to perform a queen-side castle move for the current player.
     *
     * A queen-side castle move involves moving the king two squares towards the queen-side rook,
     * and then moving the rook to the square the king crossed over.
     *
     * This function checks for the following conditions to ensure a valid queen-side castle:
     * 1. **No Check:** The current player must not be in check.
     * 2. **Castling Rights:** The current player must have queen-side castling rights.
     * 3. **Empty Squares:** The squares between the king and the queen-side rook (d, c, and b) must be empty.
     * 4. **No Check Through Squares:** The king must not be in check, nor would it pass through or end up in a square that is under attack by the opponent (d and c).
     * 5. **Rook Presence:** There must be a rook on the queen-side corner (a).
     *
     * If all conditions are met, a `BoardMove` representing the queen-side castle is returned.
     * Otherwise, null is returned, indicating an illegal or impossible move.
     *
     * @param gameSnapshotState The current state of the game board.
     * @return A `BoardMove` object representing the queen-side castle move if it is valid, or null otherwise.
     */
    private fun castleQueenSide(gameSnapshotState: GameSnapshotState): BoardMove? {
        if (gameSnapshotState.hasCheck()) return null
        if (!gameSnapshotState.castlingInfo[color].canCastleQueenSide) return null

        val rank = if (this.isWhite()) 1 else 8
        val eSquare = gameSnapshotState.board[e, rank]!!
        val dSquare = gameSnapshotState.board[d, rank]!!
        val cSquare = gameSnapshotState.board[c, rank]!!
        val bSquare = gameSnapshotState.board[b, rank]!!
        val aSquare = gameSnapshotState.board[a, rank]!!
        if (dSquare.isNotEmpty || cSquare.isNotEmpty || bSquare.isNotEmpty) return null
        if (gameSnapshotState.hasCheckFor(dSquare.position) || gameSnapshotState.hasCheckFor(cSquare.position)) return null
        if (aSquare.piece !is Rook) return null

        return BoardMove(
            move = QueenSideCastle(this, eSquare.position, cSquare.position),
            consequence = Move(aSquare.piece, aSquare.position, dSquare.position)
        )
    }

    /**
     * Attempts to perform a king-side castle move for the current player.
     *
     * This function checks if a king-side castle is legal and, if so, returns a `BoardMove`
     * representing the castle.  It considers the following conditions:
     *
     * 1. **No Check:** The current player must not be in check.
     * 2. **Castling Rights:** The current player must have the right to castle king-side.
     * 3. **Clear Path:** The squares between the king and the king-side rook (f and g files) must be empty.
     * 4. **No Check Through Path:** The squares the king moves through (f and g files) must not be under attack.
     * 5. **Rook Presence:** The king-side rook must be present on its starting square.
     *
     * If any of these conditions are not met, the function returns `null`.
     *
     * @param gameSnapshotState The current state of the game.
     * @return A `BoardMove` representing the king-side castle if legal, `null` otherwise.
     */
    private fun castleKingSide(gameSnapshotState: GameSnapshotState): BoardMove? {
        if (gameSnapshotState.hasCheck()) return null
        if (!gameSnapshotState.castlingInfo[color].canCastleKingSide) return null

        val rank = if (this.isWhite()) 1 else 8
        val eSquare = gameSnapshotState.board[e, rank]!!
        val fSquare = gameSnapshotState.board[f, rank]!!
        val gSquare = gameSnapshotState.board[g, rank]!!
        val hSquare = gameSnapshotState.board[h, rank]!!
        if (fSquare.isNotEmpty || gSquare.isNotEmpty) return null
        if (gameSnapshotState.hasCheckFor(fSquare.position) || gameSnapshotState.hasCheckFor(gSquare.position)) return null
        if (hSquare.piece !is Rook) return null

        return BoardMove(
            move = KingSideCastle(this, eSquare.position, gSquare.position),
            consequence = Move(hSquare.piece, hSquare.position, fSquare.position)
        )
    }

    companion object {
        const val SYMBOL: String = "K"
        val targets = listOf(
            Pair(1, 0),
            Pair(1, 1),
            Pair(0, 1),
            Pair(-1, 1),
            Pair(-1, 0),
            Pair(-1, -1),
            Pair(0, -1),
            Pair(1, -1)
        )
    }

}