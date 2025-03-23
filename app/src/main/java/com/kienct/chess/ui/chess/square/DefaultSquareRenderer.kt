package com.kienct.chess.ui.chess.square

import com.kienct.chess.ui.chess.square.decoration.DefaultHighlightSquare
import com.kienct.chess.ui.chess.square.decoration.DefaultSquareBackground
import com.kienct.chess.ui.chess.square.decoration.DefaultSquarePositionLabel
import com.kienct.chess.ui.chess.square.decoration.TargetMarks

/**
 * Default implementation of [SquareRenderer].
 *
 * This object provides a standard set of decorations for rendering squares,
 * including a background, highlight, position labels, and target marks.
 *
 * It serves as a convenient starting point for rendering squares with common visual elements.
 * You can customize the rendering by implementing your own [SquareRenderer] or by modifying
 * the decorations list in this object, although direct modification is not recommended.
 *
 * The default decorations are:
 *  - [DefaultSquareBackground]: Provides a basic background color for each square.
 *  - [DefaultHighlightSquare]: Highlights a square, often used to indicate selection or focus.
 *  - [DefaultSquarePositionLabel]: Displays the position label (e.g., coordinates) of the square.
 *  - [TargetMarks]:  Displays visual marks on the square to indicate some target state.
 */
object DefaultSquareRenderer : SquareRenderer {
    override val decorations: List<SquareDecoration> =
        listOf(
            DefaultSquareBackground,
            DefaultHighlightSquare,
            DefaultSquarePositionLabel,
            TargetMarks
        )
}