package com.kienct.chess.controller.state

import com.kienct.chess.model.move.AppliedMove

data class GameStateTransition(
    val fromSnapshotState: GameSnapshotState,
    val toSnapshotState: GameSnapshotState,
    val move: AppliedMove
)
