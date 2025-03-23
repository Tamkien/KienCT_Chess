package com.kienct.chess.ui.chess.board

import com.kienct.chess.ui.chess.pieces.Pieces
import com.kienct.chess.ui.chess.board.decoration.DecoratedSquares
import com.kienct.chess.ui.chess.square.DefaultSquareRenderer

object DefaultBoardRenderer : BoardRenderer {

    override val decorations: List<BoardDecoration> =
        listOf(
            DecoratedSquares(DefaultSquareRenderer.decorations),
            Pieces
        )
}