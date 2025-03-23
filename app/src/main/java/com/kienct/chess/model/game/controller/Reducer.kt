package com.kienct.chess.model.game.controller

import com.kienct.chess.model.board.Position
import com.kienct.chess.model.game.state.GameMetaInfo
import com.kienct.chess.model.game.state.GamePlayState
import com.kienct.chess.model.game.state.GameSnapshotState
import com.kienct.chess.model.game.state.GameState
import com.kienct.chess.model.game.state.PromotionState
import com.kienct.chess.model.game.state.UiState
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.piece.Piece

object Reducer {
    sealed class Action {
        data object StepForward : Action()
        data object StepBackward : Action()
        data class GoToMove(val moveIndex: Int) : Action()
        data class ResetTo(
            val gameSnapshotState: GameSnapshotState,
            val gameMetaInfo: GameMetaInfo
        ) : Action()

        data class ToggleSelectPosition(val position: Position) : Action()
        data class ApplyMove(val boardMove: BoardMove) : Action()
        data class RequestPromotion(val at: Position) : Action()
        data class PromoteTo(val piece: Piece) : Action()
    }

    /**
     * Invokes a state transition in the game based on the provided [Action].
     *
     * This function acts as the core reducer in the game's state management system. It takes the current
     * [GamePlayState] and an [Action] as input, and returns a new [GamePlayState] representing the result
     * of applying the action to the current state.
     *
     * The function handles various actions, including:
     * - **Navigation:** Stepping forward or backward through game history, jumping to a specific move.
     * - **Resetting:** Reverting to a specific game state.
     * - **UI Interaction:** Toggling the selection of a position on the board.
     * - **Applying Moves:** Executing a move and updating the game's state accordingly.
     * - **Promotion Handling:** Managing pawn promotion requests and selections.
     *
     * @param gamePlayState The current state of the game, including the game history, UI state, and promotion state.
     * @param action The action to be applied to the current game state.
     * @return A new [GamePlayState] reflecting the result of applying the action.
     */
    operator fun invoke(gamePlayState: GamePlayState, action: Action): GamePlayState =
        when (action) {
            is Action.StepForward -> gamePlayState.stepBy(1)
            is Action.StepBackward -> gamePlayState.stepBy(-1)
            is Action.GoToMove -> gamePlayState.goToSnapshot(action.moveIndex + 1)
            is Action.ResetTo -> resetTo(
                gamePlayState,
                action.gameMetaInfo,
                action.gameSnapshotState
            )

            is Action.ToggleSelectPosition -> toggleSelectPosition(gamePlayState, action.position)
            is Action.ApplyMove -> applyMove(gamePlayState, action.boardMove)
            is Action.RequestPromotion -> requestPromotion(gamePlayState, action.at)
            is Action.PromoteTo -> promoteTo(gamePlayState, action.piece)
        }

    /**
     * Resets the game to a specific state.
     *
     * @param gamePlayState The current state of the game.
     * @param gameMetaInfo The metadata of the game.
     * @param gameSnapshotState The snapshot state to reset to.
     * @return A new [GamePlayState] representing the reset game.
     */
    private fun resetTo(
        gamePlayState: GamePlayState,
        gameMetaInfo: GameMetaInfo,
        gameSnapshotState: GameSnapshotState
    ): GamePlayState {
        return GamePlayState(
            gameState = GameState(
                gameMetaInfo = gameMetaInfo,
                states = listOf(gameSnapshotState)
            )
        )
    }

    /**
     * Toggles the selection of a position on the board.
     *
     * @param gamePlayState The current state of the game.
     * @param position The position to toggle.
     * @return A new [GamePlayState] with the updated selection state.
     */
    private fun toggleSelectPosition(
        gamePlayState: GamePlayState,
        position: Position
    ): GamePlayState {
        val newSelectedPosition =
            if (gamePlayState.uiState.selectedPosition == position) null else position
        return gamePlayState.copy(uiState = gamePlayState.uiState.copy(selectedPosition = newSelectedPosition))
    }

    /**
     * Applies a move to the current game state.
     *
     * @param gamePlayState The current state of the game.
     * @param boardMove The move to apply.
     * @return A new [GamePlayState] reflecting the applied move.
     */
    private fun applyMove(gamePlayState: GamePlayState, boardMove: BoardMove): GamePlayState {
        val gameState = gamePlayState.gameState
        val currentSnapshotState = gameState.currentSnapshotState
        val states = gameState.states.toMutableList()
        val currentIndex = gameState.currentIndex

        val transition = currentSnapshotState.calculateAppliedMove(
            move = boardMove,
            previousStates = states.subList(0, currentIndex + 1)
        )

        states[currentIndex] = transition.fromSnapshotState
        val updatedStates = states.subList(0, currentIndex + 1).toMutableList().apply {
            add(transition.toSnapshotState)
        }

        return gamePlayState.copy(
            gameState = gameState.copy(
                states = updatedStates,
                currentIndex = updatedStates.lastIndex,
                lastActiveState = currentSnapshotState,
                gameMetaInfo = gameState.gameMetaInfo.withResolution(
                    resolution = transition.toSnapshotState.resolution,
                    lastMoveBy = transition.fromSnapshotState.toMove
                )
            ),
            uiState = UiState(transition.toSnapshotState),
            promotionState = PromotionState.None
        )
    }

    /**
     * Requests a pawn promotion at a specific position.
     *
     * @param gamePlayState The current state of the game.
     * @param at The position where the promotion is requested.
     * @return A new [GamePlayState] with the promotion request state.
     */
    private fun requestPromotion(gamePlayState: GamePlayState, at: Position): GamePlayState {
        return gamePlayState.copy(
            uiState = gamePlayState.uiState.copy(showPromotionDialog = true),
            promotionState = PromotionState.Await(at)
        )
    }

    /**
     * Promotes a pawn to a specific piece.
     *
     * @param gamePlayState The current state of the game.
     * @param piece The piece to promote to.
     * @return A new [GamePlayState] with the promotion applied.
     */
    private fun promoteTo(gamePlayState: GamePlayState, piece: Piece): GamePlayState {
        return gamePlayState.copy(
            uiState = gamePlayState.uiState.copy(showPromotionDialog = false),
            promotionState = PromotionState.ContinueWith(piece)
        )
    }

    /**
     * Advances the game play state by a specified number of steps.
     *
     * This function moves the current index within the game's state history forward or backward
     * by the given `step`. If the resulting index falls outside the valid range of state history,
     * the function returns the current state without any modification.
     *
     * @param step The number of steps to advance the game play state.
     *             A positive value moves forward, a negative value moves backward.
     *             Zero results in no change.
     * @return A new `GamePlayState` object representing the game state after moving by `step`,
     *         or the current `GamePlayState` if the step would result in an invalid index.
     *
     * @see goToSnapshot
     */
    private fun GamePlayState.stepBy(step: Int): GamePlayState {
        val newIndex = gameState.currentIndex + step
        if (newIndex !in 0..gameState.states.lastIndex) return this
        return goToSnapshot(newIndex)
    }

    /**
     * Navigates the game state to a specific snapshot (state history index).
     *
     * This function allows jumping to a previously recorded game state by its index.
     * It ensures the target index is within the valid range of available snapshots.
     * If the target is out of bounds, it returns the current game play state unchanged.
     *
     * @param target The index of the target snapshot within the game state's history.
     *               This should be a non-negative integer representing the index of a snapshot.
     *               Index 0 represents the initial state, and higher numbers represent later states.
     * @return A new [GamePlayState] instance representing the game at the target snapshot,
     *         or the current [GamePlayState] if the target is invalid.
     *
     * @throws IndexOutOfBoundsException if target is not in 0..gameState.states.lastIndex
     */
    private fun GamePlayState.goToSnapshot(target: Int): GamePlayState {
        if (target !in 0..gameState.states.lastIndex) return this

        return copy(
            gameState = gameState.copy(
                currentIndex = target,
                lastActiveState = gameState.currentSnapshotState
            ),
            uiState = UiState(gameState.states[target])
        )
    }
}