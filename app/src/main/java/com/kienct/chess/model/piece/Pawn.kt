package com.kienct.chess.model.piece

import com.kienct.chess.R
import com.kienct.chess.model.board.Board
import com.kienct.chess.model.board.Square
import com.kienct.chess.model.game.state.GameSnapshotState
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.move.Capture
import com.kienct.chess.model.move.Move
import com.kienct.chess.model.move.Promotion
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class Pawn(override val color: Color) : Piece {
    @IgnoredOnParcel
    override val asset: Int = if (this.isWhite()) R.drawable.pawn_light else R.drawable.pawn_dark

    @IgnoredOnParcel
    override val symbol: String = if (this.isWhite()) "♙" else "♟︎"

    @IgnoredOnParcel
    override val textSymbol: String = SYMBOL

    override fun pseudoLegalMoves(
        gameSnapshotState: GameSnapshotState,
        isChecked: Boolean
    ): List<BoardMove> {
        val board = gameSnapshotState.board
        val square = board.find(this) ?: return emptyList()
        val moves = mutableListOf<BoardMove>()

        advanceSingle(board, square)?.let { moves += it }
        advanceTwoSquares(board, square)?.let { moves += it }
        captureLeft(board, square)?.let { moves += it }
        captureRight(board, square)?.let { moves += it }
        enPassantLeft(gameSnapshotState, square)?.let { moves += it }
        enPassantRight(gameSnapshotState, square)?.let { moves += it }

        return moves.flatMap {
            it.checkForPromotion()
        }
    }

    /**
     * Attempts to advance this piece forward by one square.
     *
     * This function checks if the square directly in front of the piece (one rank forward or backward
     * depending on the piece's color) is empty. If it is, a [BoardMove] representing a single-square
     * advance is created and returned. Otherwise, null is returned, indicating that the piece cannot
     * advance in this way.
     *
     * @param board The current state of the chessboard.
     * @param square The [Square] where this piece currently resides.
     * @return A [BoardMove] representing the single-square advance if the target square is empty,
     *         or null if the target square is occupied.
     */
    private fun advanceSingle(board: Board, square: Square): BoardMove? {
        val deltaRank = if (this.isWhite()) 1 else -1
        val target = board[square.file, square.rank + deltaRank]
        return if (target?.isEmpty == true) BoardMove(
            move = Move(this, square.position, target.position)
        ) else null
    }

    /**
     * Attempts to advance the pawn two squares forward from its starting rank.
     *
     * This function checks if the pawn is on its starting rank (rank 2 for white, rank 7 for black)
     * and if the two squares directly in front of it are empty. If both conditions are met, it
     * constructs a [BoardMove] representing the two-square advance. Otherwise, it returns null.
     *
     * @param board The current state of the chessboard.
     * @param square The square where the pawn is currently located.
     * @return A [BoardMove] representing the two-square advance if valid, null otherwise.
     */
    private fun advanceTwoSquares(board: Board, square: Square): BoardMove? {
        if ((this.isWhite() && square.rank == 2) || (this.isBlack() && square.rank == 7)) {
            val deltaRank1 = if (this.isWhite()) 1 else -1
            val deltaRank2 = if (this.isWhite()) 2 else -2
            val target1 = board[square.file, square.rank + deltaRank1]
            val target2 = board[square.file, square.rank + deltaRank2]
            return if (target1?.isEmpty == true && target2?.isEmpty == true) BoardMove(
                move = Move(this, square.position, target2.position)
            ) else null
        }
        return null
    }

    private fun captureLeft(board: Board, square: Square) = capture(board, square, -1)

    private fun captureRight(board: Board, square: Square) = capture(board, square, 1)

    /**
     * Attempts to capture a piece on the board diagonally forward from the given square.
     *
     * This function checks if a piece of the opposite color exists at a diagonal
     * position relative to the provided square and, if so, returns a `BoardMove`
     * representing a capture move.
     *
     * @param board The current state of the game board.
     * @param square The square from which the potential capture move originates.
     * @param deltaFile The file offset to check for the potential capture. This should be either -1 or 1.
     *                  -1 indicates a diagonal move to the left, and 1 indicates a diagonal move to the right.
     * @return A `BoardMove` representing the capture if a piece of the opposite color is present
     *         at the target diagonal position; otherwise, `null`.
     *
     * @throws IllegalArgumentException if `deltaFile` is not -1 or 1
     *
     * @see Board
     * @see Square
     * @see BoardMove
     * @see Move
     * @see Capture
     * @see Piece.isWhite
     */
    private fun capture(board: Board, square: Square, deltaFile: Int): BoardMove? {
        val deltaRank = if (this.isWhite()) 1 else -1
        val target = board[square.file + deltaFile, square.rank + deltaRank]
        return if (target?.hasPiece(color.opposite()) == true) BoardMove(
            move = Move(this, square.position, target.position),
            preMove = Capture(target.piece!!, target.position)
        ) else null
    }

    private fun enPassantLeft(gameSnapshotState: GameSnapshotState, square: Square) =
        enPassant(gameSnapshotState, square, -1)

    private fun enPassantRight(gameSnapshotState: GameSnapshotState, square: Square) =
        enPassant(gameSnapshotState, square, 1)

    /**
     * Determines if an en passant capture is possible for the current piece.
     *
     * En passant is a special pawn capture that can only occur immediately after a pawn
     * advances two squares from its starting rank, and an enemy pawn could have captured
     * it had it only advanced one square.
     *
     * @param gameSnapshotState The current state of the game, including the board and move history.
     * @param square The square where the potential capturing pawn is located.
     * @param deltaFile The change in file (-1 or 1) representing the direction of the potential
     *                  en passant capture (left or right).
     * @return A [BoardMove] representing the en passant capture if it's valid, otherwise null.
     *
     * @throws IllegalStateException if the en passant target or the captured piece square are null,
     * which indicates an internal board state error.
     *
     * Conditions for en passant to be valid:
     * 1. The capturing pawn must be on its fifth rank (white) or fourth rank (black).
     * 2. The last move must have been a pawn move.
     * 3. The last move must have been a two-square advance from the pawn's starting rank.
     * 4. The target square of the two-square move must be on the same rank as the capturing pawn.
     * 5. The target square of the two-square move must be on an adjacent file to the capturing pawn.
     *
     * If all conditions are met, a [BoardMove] is constructed with:
     * - The [Move] component representing the movement of the capturing pawn to the en passant target square.
     * - The [Capture] component representing the removal of the captured pawn from its original square.
     *
     * Example for White:
     * - Capturing pawn is on rank 5.
     * - Last move was a black pawn moving from rank 7 to rank 5 (two squares).
     * - The black pawn ended on an adjacent file to the capturing pawn.
     *
     * Example for Black:
     * - Capturing pawn is on rank 4.
     * - Last move was a white pawn moving from rank 2 to rank 4 (two squares).
     * - The white pawn ended on an adjacent file to the capturing pawn.
     */
    private fun enPassant(
        gameSnapshotState: GameSnapshotState,
        square: Square,
        deltaFile: Int
    ): BoardMove? {
        if (square.position.rank != if (this.isWhite()) 5 else 4) return null
        val lastMove = gameSnapshotState.lastMove ?: return null
        if (lastMove.piece !is Pawn) return null
        val fromInitialSquare = (lastMove.from.rank == if (this.isWhite()) 7 else 2)
        val twoSquareMove = (lastMove.to.rank == square.position.rank)
        val isOnNextFile = lastMove.to.file == square.file + deltaFile

        return if (fromInitialSquare && twoSquareMove && isOnNextFile) {
            val deltaRank = if (this.isWhite()) 1 else -1
            val enPassantTarget =
                gameSnapshotState.board[square.file + deltaFile, square.rank + deltaRank]
            val capturedPieceSquare = gameSnapshotState.board[square.file + deltaFile, square.rank]
            requireNotNull(enPassantTarget)
            requireNotNull(capturedPieceSquare)

            BoardMove(
                move = Move(this, square.position, enPassantTarget.position),
                preMove = Capture(capturedPieceSquare.piece!!, capturedPieceSquare.position)
            )
        } else null
    }


    companion object {
        const val SYMBOL: String = ""
    }
}

private fun BoardMove.checkForPromotion(): List<BoardMove> =
    if (move.to.rank == if (piece.isWhite()) 8 else 1) {
        listOf(
            copy(consequence = Promotion(move.to, Queen(piece.color))),
            copy(consequence = Promotion(move.to, Rook(piece.color))),
            copy(consequence = Promotion(move.to, Bishop(piece.color))),
            copy(consequence = Promotion(move.to, Knight(piece.color))),
        )
    } else listOf(this)