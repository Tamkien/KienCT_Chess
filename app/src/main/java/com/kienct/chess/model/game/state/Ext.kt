package com.kienct.chess.model.game.state

import com.kienct.chess.model.board.Position
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.move.BoardMove.Ambiguity.AMBIGUOUS_FILE
import com.kienct.chess.model.move.BoardMove.Ambiguity.AMBIGUOUS_RANK
import com.kienct.chess.model.piece.Bishop
import com.kienct.chess.model.piece.King
import com.kienct.chess.model.piece.Knight
import com.kienct.chess.model.piece.Piece
import com.kienct.chess.model.piece.isBlack
import com.kienct.chess.model.piece.isWhite
import java.util.EnumSet

/**
 * Applies ambiguity flags to a [BoardMove] based on the game state.
 *
 * This function determines if a move's origin is ambiguous (i.e., multiple pieces of the same type
 * could have made the move) and, if so, which ambiguity flags should be set.
 *
 * The ambiguity is resolved by checking if other pieces of the same type as the moving piece can
 * also reach the same destination square. If multiple pieces can reach the destination, ambiguity
 * flags are added.
 *
 * Ambiguity flags are:
 *  - [AMBIGUOUS_FILE]: If multiple pieces can reach the destination, and only one of them is on the same file as the original piece's origin.
 *  - [AMBIGUOUS_RANK]: If multiple pieces can reach the destination, and only one of them is on the same rank as the original piece's origin.
 *  - Both [AMBIGUOUS_FILE] and [AMBIGUOUS_RANK]: If multiple pieces can reach the destination, and none of the rules above apply.
 *
 * If only one piece of that type can reach the target, there is no ambiguity and the original [BoardMove] is returned.
 *
 * @param snapshot The current game state [GameSnapshotState].
 * @return A [BoardMove] with updated ambiguity flags, or the original [BoardMove] if no ambiguity is detected.
 * @see BoardMove.Ambiguity
 */
fun BoardMove.applyAmbiguity(snapshot: GameSnapshotState): BoardMove =
    snapshot.board
        .pieces(snapshot.toMove)
        .asSequence()
        .filter { (_, piece) -> piece.textSymbol == this.piece.textSymbol && piece.color == this.piece.color }
        .flatMap { (_, piece) -> piece.pseudoLegalMoves(snapshot, false) }
        .filter { it.to == this.to }
        .map { it.from }
        .distinct() // promotion moves have same `from`, but are different per target piece, we don't need all of those
        .toList()
        .let { similarPiecePositions ->
            val ambiguityFlags = EnumSet.noneOf(BoardMove.Ambiguity::class.java)
            when (similarPiecePositions.size) {
                1 -> this
                else -> {
                    val onSameFile = similarPiecePositions.filter { it.file == from.file }
                    if (onSameFile.size == 1) { // only one piece can reach the target on the same file
                        ambiguity.add(AMBIGUOUS_FILE)
                    } else { // multiple pieces can reach the target on the same file
                        val onSameRank = similarPiecePositions.filter { it.rank == from.rank }
                        if (onSameRank.size == 1) { // only one piece can reach the target on the same rank
                            ambiguity.add(AMBIGUOUS_RANK)
                        } else {
                            ambiguity.add(AMBIGUOUS_FILE)
                            ambiguity.add(AMBIGUOUS_RANK)
                        }
                    }
                    copy(ambiguity = ambiguityFlags)
                }
            }
        }

fun Map<Position, Piece>.hasInsufficientMaterial(): Boolean =
    when {
        size == 2 && hasWhiteKing() && hasBlackKing() -> true
        size == 3 && hasWhiteKing() && hasBlackKing() && hasBishop() -> true
        size == 3 && hasWhiteKing() && hasBlackKing() && hasKnight() -> true
        size == 4 && hasWhiteKing() && hasBlackKing() && hasBishopsOnSameColor() -> true
        else -> false
    }

private fun Map<Position, Piece>.hasWhiteKing(): Boolean =
    values.find { it.isWhite() && it is King } != null

private fun Map<Position, Piece>.hasBlackKing(): Boolean =
    values.find { it.isBlack() && it is King } != null

private fun Map<Position, Piece>.hasBishop(): Boolean =
    values.find { it is Bishop } != null

private fun Map<Position, Piece>.hasKnight(): Boolean =
    values.find { it is Knight } != null

private fun Map<Position, Piece>.hasBishopsOnSameColor(): Boolean {
    val bishops = filter { it.value is Bishop }

    return bishops.size > 1 && (bishops.all { it.key.isLightSquare() } || bishops.all { it.key.isDarkSquare() })
}

fun Position.isLightSquare(): Boolean =
    (ordinal + file) % 2 == 0

fun Position.isDarkSquare(): Boolean =
    (ordinal + file) % 2 == 1

fun List<GameSnapshotState>.hasThreefoldRepetition(): Boolean =
    map { it.toRepetitionRelevantState().hashCode() }
        .groupBy { it }
        .map { it.value.size }
        .any { it > 2 }