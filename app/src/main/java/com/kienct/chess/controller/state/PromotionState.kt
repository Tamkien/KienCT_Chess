package com.kienct.chess.controller.state

import android.os.Parcelable
import com.kienct.chess.model.board.Position
import com.kienct.chess.model.piece.Piece
import kotlinx.parcelize.Parcelize

sealed class PromotionState : Parcelable {
    @Parcelize
    data object None : PromotionState()

    @Parcelize
    data class Await(val position: Position) : PromotionState()

    @Parcelize
    data class ContinueWith(val piece: Piece) : PromotionState()
}
