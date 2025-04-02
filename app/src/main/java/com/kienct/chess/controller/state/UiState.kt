package com.kienct.chess.controller.state

import android.os.Parcelable
import com.kienct.chess.model.board.Position
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.move.Capture
import com.kienct.chess.model.move.targetPositions
import com.kienct.chess.model.piece.King
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UiState(
    private val gameSnapshotState: GameSnapshotState,
    val selectedPosition: Position? = null,
    val showPromotionDialog: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    private val toMove = gameSnapshotState.toMove

    @IgnoredOnParcel
    private val lastMovePositions: List<Position> =
        gameSnapshotState.lastMove?.let { listOf(it.from, it.to) } ?: emptyList()

    @IgnoredOnParcel
    private val uiSelectedPositions: List<Position> =
        selectedPosition?.let { listOf(it) } ?: emptyList()

    @IgnoredOnParcel
    val highlightedPositions: List<Position> =
        lastMovePositions + uiSelectedPositions

    @IgnoredOnParcel
    private val ownPiecePositions: List<Position> =
        gameSnapshotState.board.pieces
            .filter { (_, piece) -> piece.color == toMove }
            .map { it.key }

    @IgnoredOnParcel
    val possibleCaptures: List<Position> =
        possibleMoves { it.preMove is Capture }.targetPositions()

    @IgnoredOnParcel
    val possibleMovesWithoutCaptures: List<Position> =
        possibleMoves { it.preMove !is Capture }.targetPositions()

    @IgnoredOnParcel
    val clickablePositions: List<Position> =
        ownPiecePositions +
                possibleCaptures +
                possibleMovesWithoutCaptures

    fun possibleMoves(predicate: (BoardMove) -> Boolean = { true }) =
        selectedPosition?.let {
            gameSnapshotState.legalMovesFrom(it)
                .filter(predicate)
        } ?: emptyList()

    @IgnoredOnParcel
    val checkedKingPosition: Position? =
        if (gameSnapshotState.hasCheck()) {
            gameSnapshotState.board.pieces
                .filter { (_, piece) -> piece is King && piece.color == toMove }
                .keys.firstOrNull()
        } else {
            null
        }

}