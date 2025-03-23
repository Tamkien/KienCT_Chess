package com.kienct.chess.model.move

import com.kienct.chess.model.board.Position

fun List<BoardMove>.targetPositions(): List<Position> =
    map { it.to }