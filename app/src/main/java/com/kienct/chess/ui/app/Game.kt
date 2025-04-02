package com.kienct.chess.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kienct.chess.R
import com.kienct.chess.controller.core.GameController
import com.kienct.chess.controller.state.GamePlayState
import com.kienct.chess.ui.chess.Board
import com.kienct.chess.ui.dialogs.PromotionDialog

@Composable
fun Game(state: GamePlayState = GamePlayState()) {
    var isFlipped by rememberSaveable { mutableStateOf(false) }
    val gamePlayState = rememberSaveable { mutableStateOf(state) }

    val gameController = remember {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Board(
            gamePlayState = gamePlayState.value,
            gameController = gameController,
            isFlipped = isFlipped
        )
        GameControls(
            gamePlayState = gamePlayState.value,
            onStepBack = { gameController.stepBackward() },
            onStepForward = { gameController.stepForward() },
            onFlipBoard = { isFlipped = !isFlipped },
            onReset = { gameController.reset() },
        )
    }

    GameDialogs(
        gamePlayState = gamePlayState,
        gameController = gameController,
    )

}

/**
 * Composable function that displays the game controls for a chess game.
 *
 * @param gamePlayState The current state of the game play, including the game state and move history.
 * @param onStepBack Callback function to be executed when the "Step Back" button is clicked.
 *                   This should navigate the game to the previous move.
 * @param onStepForward Callback function to be executed when the "Step Forward" button is clicked.
 *                      This should navigate the game to the next move.
 * @param onFlipBoard Callback function to be executed when the "Flip Board" button is clicked.
 *                    This should rotate the chessboard.
 * @param onReset Callback function to be executed when the "Reset" button is clicked.
 *                This should reset the game to the initial state.
 */
@Composable
fun GameControls(
    gamePlayState: GamePlayState,
    onStepBack: () -> Unit,
    onStepForward: () -> Unit,
    onFlipBoard: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(
            onClick = onStepBack,
            enabled = gamePlayState.gameState.hasPrevIndex
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_chevron_left_24),
                tint = LocalTextStyle.current.color,
                contentDescription = stringResource(R.string.action_previous_move)
            )
        }
        Button(
            onClick = onStepForward,
            enabled = gamePlayState.gameState.hasNextIndex
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_chevron_right_24),
                tint = LocalTextStyle.current.color,
                contentDescription = stringResource(R.string.action_next_move)
            )
        }
        Button(
            onClick = onFlipBoard,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.rounded_autorenew_24),
                tint = LocalTextStyle.current.color,
                contentDescription = stringResource(R.string.action_flip)
            )
        }
        Button(
            onClick = onReset,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.rounded_close_24),
                tint = LocalTextStyle.current.color,
                contentDescription = stringResource(R.string.action_flip)
            )
        }
    }
}

/**
 * Composable function responsible for managing and displaying game-related dialogs.
 *
 * This function currently handles the display of the promotion dialog, which appears
 * when a pawn reaches the opposite end of the board and needs to be promoted to another piece.
 * It uses the [ManagedPromotionDialog] composable to handle the promotion logic and UI.
 *
 * @param gamePlayState A [MutableState] object holding the current state of the game play.
 *   This state should include information about the UI state, such as whether the promotion dialog
 *   should be displayed.
 * @param gameController An instance of [GameController], responsible for handling game logic,
 *   including pawn promotion.  The [ManagedPromotionDialog] will interact with this controller
 *   to update the game state upon a promotion selection.
 */
@Composable
fun GameDialogs(
    gamePlayState: MutableState<GamePlayState>,
    gameController: GameController
) {
    ManagedPromotionDialog(
        showPromotionDialog = gamePlayState.value.uiState.showPromotionDialog,
        gameController = gameController
    )
}

/**
 * Displays a promotion dialog when a pawn reaches the opposite end of the board, allowing the player
 * to choose a piece to promote the pawn to (Queen, Rook, Bishop, or Knight).
 *
 * @param showPromotionDialog A boolean indicating whether the promotion dialog should be displayed.
 * @param gameController The [GameController] managing the current game state and actions.  The
 *  [GameController.toMove] property indicates the current player whose pawn is being promoted. The
 *  dialog uses the [GameController.onPromotionPieceSelected] to handle the piece selection.
 */
@Composable
fun ManagedPromotionDialog(showPromotionDialog: Boolean, gameController: GameController) {
    if (showPromotionDialog) {
        PromotionDialog(gameController.toMove) {
            gameController.onPromotionPieceSelected(it)
        }
    }
}
