package com.kienct.chess.model.game.controller

import com.kienct.chess.model.board.Position
import com.kienct.chess.model.board.Square
import com.kienct.chess.model.game.Resolution
import com.kienct.chess.model.game.controller.Reducer.Action
import com.kienct.chess.model.game.state.GameMetaInfo
import com.kienct.chess.model.game.state.GamePlayState
import com.kienct.chess.model.game.state.GameSnapshotState
import com.kienct.chess.model.game.state.PromotionState
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.move.Promotion
import com.kienct.chess.model.move.targetPositions
import com.kienct.chess.model.piece.Color
import com.kienct.chess.model.piece.Piece

class GameController(
    val getGamePlayState: () -> GamePlayState,
    private val setGamePlayState: ((GamePlayState) -> Unit)? = null
) {
    private val gamePlayState: GamePlayState
        get() = getGamePlayState()

    private val gameSnapshotState: GameSnapshotState
        get() = gamePlayState.gameState.currentSnapshotState

    val toMove: Color
        get() = gameSnapshotState.toMove

    fun square(position: Position): Square =
        gameSnapshotState.board[position]

    private fun Position.hasOwnPiece() =
        square(this).hasPiece(gameSnapshotState.toMove)

    fun onClick(position: Position) {
        if (gameSnapshotState.resolution != Resolution.IN_PROGRESS) return
        if (position.hasOwnPiece()) {
            toggleSelectPosition(position)
        } else if (canMoveTo(position)) {
            val selectedPosition = gamePlayState.uiState.selectedPosition
            requireNotNull(selectedPosition)
            applyMove(selectedPosition, position)
        }
    }

    private fun toggleSelectPosition(position: Position) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Action.ToggleSelectPosition(position))
        )
    }

    private fun canMoveTo(position: Position) =
        position in gamePlayState.uiState.possibleMoves().targetPositions()

    private fun applyMove(from: Position, to: Position) {
        val boardMove = findBoardMove(from, to) ?: return
        applyMove(boardMove)
    }

    private fun applyMove(boardMove: BoardMove) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Action.ApplyMove(boardMove))
        )
    }

    /**
     * Finds a valid [BoardMove] from a given starting [Position] to a target [Position].
     *
     * This function determines if a move from the `from` position to the `to` position is legal
     * within the current game state (`gameSnapshotState`). It handles different scenarios, including
     * no legal moves, a single legal move, and promotion moves.
     *
     * @param from The starting [Position] of the move.
     * @param to The target [Position] of the move.
     * @return A [BoardMove] object representing the legal move from `from` to `to`, or null if no move is legal.
     * @throws IllegalArgumentException If there are no legal moves from the `from` position to the `to` position.
     * @throws IllegalStateException If there are multiple legal moves that are not all promotions.
     *
     * The function logic is as follows:
     * 1. It fetches all legal moves starting from the `from` position using `gameSnapshotState.legalMovesFrom(from)`.
     * 2. It filters these moves to keep only those that end at the `to` position.
     * 3. It then checks the number of remaining legal moves:
     *    - If there are no legal moves (`legalMoves.isEmpty()`), it throws an `IllegalArgumentException`.
     *    - If there is exactly one legal move (`legalMoves.size == 1`), it returns that move.
     *    - If all legal moves are promotions (`legalMoves.all { it.consequence is Promotion }`), it calls `handlePromotion` to determine the correct promotion move.
     *    - If there are multiple legal moves but not all are promotions, it throws an `IllegalStateException`, as this scenario is unexpected.
     */
    private fun findBoardMove(from: Position, to: Position): BoardMove? {
        val legalMoves = gameSnapshotState
            .legalMovesFrom(from)
            .filter { it.to == to }

        return when {
            legalMoves.isEmpty() -> {
                throw IllegalArgumentException("No legal moves exist between $from -> $to")
            }

            legalMoves.size == 1 -> {
                legalMoves.first()
            }

            legalMoves.all { it.consequence is Promotion } -> {
                handlePromotion(to, legalMoves)
            }

            else -> {
                throw IllegalStateException("Legal moves: $legalMoves")
            }
        }
    }

    /**
     * Handles the promotion logic for a pawn.
     *
     * This function manages the different states of pawn promotion within the game.
     *
     * - **PromotionState.None:** If no promotion is currently in progress, it triggers a request for promotion at the specified position.
     * - **PromotionState.Await:** If the game is awaiting a promotion choice from the user, an `IllegalStateException` is thrown, as this should not be called.
     * - **PromotionState.ContinueWith:** If a specific promotion piece has been selected, it searches the list of legal moves for a move that results in the pawn being promoted to the selected piece.
     *
     * @param at The position where the pawn is being promoted.
     * @param legalMoves The list of legal moves available in the current game state.
     * @return The `BoardMove` representing the promotion if a promotion is to be executed based on a selection, otherwise `null`.
     * @throws IllegalStateException if the function is called when the `promotionState` is `PromotionState.Await`, meaning a promotion selection is expected from the user but not provided.
     */
    private fun handlePromotion(at: Position, legalMoves: List<BoardMove>): BoardMove? {
        when (val promotionState = gamePlayState.promotionState) {
            is PromotionState.None -> {
                setGamePlayState?.invoke(
                    Reducer(gamePlayState, Action.RequestPromotion(at))
                )
            }

            is PromotionState.Await -> {
                throw IllegalStateException()
            }

            is PromotionState.ContinueWith -> {
                return legalMoves.find { move ->
                    (move.consequence as Promotion).let {
                        it.piece::class == promotionState.piece::class
                    }
                }
            }
        }

        return null
    }

    /**
     * Handles the selection of a piece during a pawn promotion.
     *
     * This function is called when the user selects a piece to promote a pawn to.
     * It updates the game state to reflect the selected promotion and then triggers
     * the `onClick` callback with the original position of the pawn that is being promoted.
     *
     * @param piece The [Piece] that the pawn is being promoted to (e.g., Queen, Rook, Knight, Bishop).
     * @throws IllegalStateException If the game is not in the expected [PromotionState.Await] state.
     */
    fun onPromotionPieceSelected(piece: Piece) {
        val state = gamePlayState.promotionState
        if (state !is PromotionState.Await) error("Not in expected state: $state")
        val position = state.position
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Action.PromoteTo(piece))
        )
        onClick(position)
    }

    fun stepForward() {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Action.StepForward)
        )
    }

    fun stepBackward() {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Action.StepBackward)
        )
    }

    fun goToMove(index: Int) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Action.GoToMove(index))
        )
    }

    fun reset(
        gameSnapshotState: GameSnapshotState = GameSnapshotState(),
        gameMetaInfo: GameMetaInfo = GameMetaInfo.createWithDefaults()

    ) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Action.ResetTo(gameSnapshotState, gameMetaInfo))
        )
    }
}