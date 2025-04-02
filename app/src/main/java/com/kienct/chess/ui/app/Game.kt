package com.kienct.chess.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kienct.chess.controller.core.GameController
import com.kienct.chess.controller.state.GamePlayState
import com.kienct.chess.ui.chess.Board
import com.kienct.chess.ui.dialogs.PromotionDialog

@Composable
fun Game(
    state: GamePlayState = GamePlayState()
) {
    var isFlipped by rememberSaveable { mutableStateOf(false) }
    val gamePlayState = rememberSaveable { mutableStateOf(state) }
    val showGameDialog = remember { mutableStateOf(false) }

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
    }

    GameDialogs(
        gamePlayState = gamePlayState,
        gameController = gameController,
    )

}

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

@Composable
fun ManagedPromotionDialog(showPromotionDialog: Boolean, gameController: GameController) {
    if (showPromotionDialog) {
        PromotionDialog(gameController.toMove) {
            gameController.onPromotionPieceSelected(it)
        }
    }
}
