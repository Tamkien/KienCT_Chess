package com.kienct.chess.model.move

import com.kienct.chess.model.board.Position

data class MoveIntention(
    val from: Position,
    val to: Position
)