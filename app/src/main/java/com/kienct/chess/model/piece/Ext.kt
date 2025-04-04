package com.kienct.chess.model.piece

import com.kienct.chess.model.board.Board
import com.kienct.chess.model.board.Square
import com.kienct.chess.controller.state.GameSnapshotState
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.move.Capture
import com.kienct.chess.model.move.Move

/**
 * Calculates all possible moves for a piece along straight lines in specified directions.
 *
 * This function determines the valid moves for a given piece by traversing the board in a straight line
 * along each direction specified in the `directions` list. It continues moving in a given direction until it
 * encounters an obstacle (another piece or the edge of the board).
 *
 * @param gameSnapshotState The current state of the game, including the board configuration.
 * @param directions A list of pairs representing the directions to explore. Each pair (dx, dy)
 *                   indicates a change in x and y coordinates, respectively, to move one step
 *                   in a specific direction. For example, (1, 0) represents moving one step to the right,
 *                   and (0, -1) represents moving one step up.
 * @return A list of [BoardMove] objects representing all the valid moves found along the lines.
 *         Returns an empty list if the piece is not found on the board.
 */
fun Piece.lineMoves(gameSnapshotState: GameSnapshotState, directions: List<Pair<Int, Int>>): List<BoardMove> {
    val moves = mutableListOf<BoardMove>()
    val board = gameSnapshotState.board
    val square = board.find(this) ?: return emptyList()

    directions.map {
        moves += lineMoves(board, square, it.first, it.second)
    }
    return moves
}

/**
 * Calculates a single capture move for a piece, given the game state and file/rank deltas.
 *
 * This function determines if a piece can perform a capture move based on the provided
 * file and rank deltas.  It checks if the target square exists, if it contains an opponent's piece
 * (or is empty), and returns a `BoardMove` representing the capture if valid.
 *
 * @param gameSnapshotState The current state of the game.
 * @param deltaFile The change in file (column) position.  Positive values move right, negative values move left.
 * @param deltaRank The change in rank (row) position. Positive values move up, negative values move down.
 * @return A `BoardMove` object representing the capture move if valid, or `null` otherwise.
 *         The returned `BoardMove` will include a `Capture` object in its `preMove` if an opponent's piece
 *         is captured, otherwise `preMove` will be `null`.
 *
 *         A move is invalid and `null` will be returned if:
 *           - The piece is not found on the board.
 *           - The target square is out of bounds.
 *           - The target square contains a piece of the same color as the moving piece.
 */
fun Piece.singleCaptureMove(
    gameSnapshotState: GameSnapshotState,
    deltaFile: Int,
    deltaRank: Int
): BoardMove? {
    val board = gameSnapshotState.board
    val square = board.find(this) ?: return null
    val target = board[square.file + deltaFile, square.rank + deltaRank] ?: return null

    return when {
        target.hasPiece(color) -> null
        else -> BoardMove(
            move = Move(
                piece = this,
                square.position,
                target.position
            ),
            preMove = when {
                target.isNotEmpty -> Capture(target.piece!!, target.position)
                else -> null
            }
        )
    }
}

fun Piece.isWhite(): Boolean = color == Color.WHITE

fun Piece.isBlack(): Boolean = color == Color.BLACK

/**
 * Calculates the possible moves along a line from a given square.
 *
 * @param board The game board.
 * @param square The starting square.
 * @param deltaFile The change in file per step.
 * @param deltaRank The change in rank per step.
 * @return A list of possible moves along the line.
 * @throws IllegalArgumentException if the square does not contain a piece.
 */
fun lineMoves(
    board: Board,
    square: Square,
    deltaFile: Int,
    deltaRank: Int
): List<BoardMove> {
    requireNotNull(square.piece) { "The starting square must contain a piece." }

    val pieceColor = square.piece.color
    val possibleMoves = mutableListOf<BoardMove>()
    val startFile = square.position.file
    val startRank = square.position.rank

    for (step in 1..7) { // Maximum 7 steps in any direction on an 8x8 board
        val targetFile = startFile + deltaFile * step
        val targetRank = startRank + deltaRank * step

        val targetSquare = board[targetFile, targetRank] ?: break // Stop if out of bounds

        if (targetSquare.hasPiece(pieceColor)) break // Stop if we hit a piece of the same color


        val move = Move(
            piece = square.piece,
            from = square.position,
            to = targetSquare.position
        )

        if (targetSquare.isEmpty) {
            possibleMoves.add(BoardMove(move))
        } else if (targetSquare.hasPiece(pieceColor.opposite())) {
            possibleMoves.add(
                BoardMove(
                    move = move,
                    preMove = Capture(targetSquare.piece!!, targetSquare.position)
                )
            )
            break // Stop after capturing an opponent's piece
        }
    }

    return possibleMoves
}