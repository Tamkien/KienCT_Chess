package com.kienct.chess.model.piece

enum class Color {
    WHITE, BLACK;
    fun opposite() =
        when (this) {
            WHITE -> BLACK
            BLACK -> WHITE
        }
}