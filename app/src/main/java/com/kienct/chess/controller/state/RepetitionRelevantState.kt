package com.kienct.chess.controller.state

import android.os.Parcelable
import com.kienct.chess.model.board.Board
import com.kienct.chess.model.piece.Color
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepetitionRelevantState(
    val board: Board,
    val toMove: Color,
    val castlingInfo: CastlingInfo,
) : Parcelable
