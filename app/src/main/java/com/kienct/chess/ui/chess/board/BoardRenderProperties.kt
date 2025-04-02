package com.kienct.chess.ui.chess.board

import androidx.compose.ui.unit.Dp
import com.kienct.chess.model.board.Position
import com.kienct.chess.controller.state.GameSnapshotState
import com.kienct.chess.controller.state.UiState

data class BoardRenderProperties(
    val fromState: GameSnapshotState,
    val toState: GameSnapshotState,
    val uiState: UiState,
    val isFlipped: Boolean,
    val squareSize: Dp,
    val onClick: (Position) -> Unit,
)