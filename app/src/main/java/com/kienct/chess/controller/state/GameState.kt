package com.kienct.chess.controller.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val gameMetaInfo: GameMetaInfo,
    val states: List<GameSnapshotState> = listOf(GameSnapshotState()),
    val currentIndex: Int = 0,
    val lastActiveState: GameSnapshotState = states.first(),
) : Parcelable {
    val currentSnapshotState: GameSnapshotState
        get() = states[currentIndex]
}