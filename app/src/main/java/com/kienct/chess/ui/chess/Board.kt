package com.kienct.chess.ui.chess

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.min
import com.kienct.chess.model.board.Position
import com.kienct.chess.controller.core.GameController
import com.kienct.chess.controller.state.GamePlayState
import com.kienct.chess.controller.state.GameSnapshotState
import com.kienct.chess.controller.state.UiState
import com.kienct.chess.ui.chess.board.BoardRenderProperties
import com.kienct.chess.ui.chess.board.DefaultBoardRenderer

/**
 * Displays the game board based on the provided game state and handles user interactions.
 *
 * This composable function acts as a high-level representation of the game board, taking the overall
 * game state and a controller to handle user input. It then delegates the rendering to a lower-level
 * `Board` composable, passing the necessary details.
 *
 * @param gamePlayState The current state of the game, including the game's history, the current
 *                      snapshot, and UI related information.
 * @param gameController The controller responsible for handling user interactions with the board.
 * @param isFlipped A boolean flag indicating whether the board should be displayed flipped.
 *                  Defaults to `false`.
 *
 * @see Board
 * @see GamePlayState
 * @see GameController
 */
@Composable
fun Board(
    gamePlayState: GamePlayState,
    gameController: GameController,
    isFlipped: Boolean = false,
) {
    Board(
        fromState = gamePlayState.gameState.lastActiveState,
        toState = gamePlayState.gameState.currentSnapshotState,
        uiState = gamePlayState.uiState,
        isFlipped = isFlipped,
        onClick = { position -> gameController.onClick(position) }
    )
}

/**
 * Renders the chess board and its decorations based on the provided game states and UI state.
 *
 * @param fromState The starting game state for animation or highlighting purposes.
 * @param toState The target game state representing the current board position.
 * @param uiState The current UI state, including things like selected pieces or legal moves.
 * @param isFlipped Whether the board should be flipped (black's perspective). Defaults to false (white's perspective).
 * @param onClick Callback function invoked when a square on the board is clicked. It provides the clicked [Position].
 */
@Composable
fun Board(
    fromState: GameSnapshotState,
    toState: GameSnapshotState,
    uiState: UiState,
    isFlipped: Boolean = false,
    onClick: (Position) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .aspectRatio(1f)
    ) {
        val boardProperties =
            BoardRenderProperties(
                fromState = fromState,
                toState = toState,
                uiState = uiState,
                squareSize = min(maxWidth, maxHeight) / 8,
                isFlipped = isFlipped,
                onClick = onClick
            )

        DefaultBoardRenderer.decorations.forEach {
            it.render(properties = boardProperties)
        }
    }
}