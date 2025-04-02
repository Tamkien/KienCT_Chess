package com.kienct.chess.controller.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents the overall state of a game, encompassing its metadata and a history of snapshots.
 *
 * @property gameMetaInfo Metadata about the game, such as player information, game type, etc.
 * @property states A list of [GameSnapshotState] objects, each representing a specific state or snapshot of the game at a particular point in time. Defaults to a list containing a single, initial [GameSnapshotState].
 * @property currentIndex The index of the currently active snapshot within the `states` list. Defaults to 0, indicating the first snapshot.
 * @property lastActiveState The last snapshot that was actively interacted with or displayed. Defaults to the first snapshot in the `states` list.  This property might be used to track the most recent state viewed, even if it's not the currently active state based on `currentIndex`.
 *
 * The `GameState` class provides properties for accessing the current snapshot and navigating through the history of snapshots:
 *  - `currentSnapshotState`: Returns the [GameSnapshotState] at the `currentIndex`.
 *  - `hasPrevIndex`: Returns `true` if there is a snapshot before the current one in the history.
 *  - `hasNextIndex`: Returns `true` if there is a snapshot after the current one in the history.
 */
@Parcelize
data class GameState(
    val gameMetaInfo: GameMetaInfo,
    val states: List<GameSnapshotState> = listOf(GameSnapshotState()),
    val currentIndex: Int = 0,
    val lastActiveState: GameSnapshotState = states.first(),
) : Parcelable {
    val currentSnapshotState: GameSnapshotState
        get() = states[currentIndex]
    val hasPrevIndex: Boolean
        get() = currentIndex > 0
    val hasNextIndex: Boolean
        get() = currentIndex < states.lastIndex
}