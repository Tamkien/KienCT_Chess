package com.kienct.chess.model.board

import android.os.Parcelable
import com.kienct.chess.model.board.Position.a1
import com.kienct.chess.model.board.Position.a2
import com.kienct.chess.model.board.Position.a7
import com.kienct.chess.model.board.Position.a8
import com.kienct.chess.model.board.Position.b1
import com.kienct.chess.model.board.Position.b2
import com.kienct.chess.model.board.Position.b7
import com.kienct.chess.model.board.Position.b8
import com.kienct.chess.model.board.Position.c1
import com.kienct.chess.model.board.Position.c2
import com.kienct.chess.model.board.Position.c7
import com.kienct.chess.model.board.Position.c8
import com.kienct.chess.model.board.Position.d1
import com.kienct.chess.model.board.Position.d2
import com.kienct.chess.model.board.Position.d7
import com.kienct.chess.model.board.Position.d8
import com.kienct.chess.model.board.Position.e1
import com.kienct.chess.model.board.Position.e2
import com.kienct.chess.model.board.Position.e7
import com.kienct.chess.model.board.Position.e8
import com.kienct.chess.model.board.Position.f1
import com.kienct.chess.model.board.Position.f2
import com.kienct.chess.model.board.Position.f7
import com.kienct.chess.model.board.Position.f8
import com.kienct.chess.model.board.Position.g1
import com.kienct.chess.model.board.Position.g2
import com.kienct.chess.model.board.Position.g7
import com.kienct.chess.model.board.Position.g8
import com.kienct.chess.model.board.Position.h1
import com.kienct.chess.model.board.Position.h2
import com.kienct.chess.model.board.Position.h7
import com.kienct.chess.model.board.Position.h8
import com.kienct.chess.model.move.PieceEffect
import com.kienct.chess.model.piece.Bishop
import com.kienct.chess.model.piece.Color
import com.kienct.chess.model.piece.Color.BLACK
import com.kienct.chess.model.piece.Color.WHITE
import com.kienct.chess.model.piece.King
import com.kienct.chess.model.piece.Knight
import com.kienct.chess.model.piece.Pawn
import com.kienct.chess.model.piece.Piece
import com.kienct.chess.model.piece.Queen
import com.kienct.chess.model.piece.Rook
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    val pieces: Map<Position, Piece>
) : Parcelable {
    constructor() : this(
        pieces = initialPieces
    )

    @IgnoredOnParcel
    val squares = Position.entries.associateWith { position ->
        Square(position, pieces[position])
    }

    operator fun get(position: Position): Square =
        squares[position]!!

    operator fun get(file: File, rank: Int): Square? =
        get(file.ordinal + 1, rank)

    operator fun get(file: Int, rank: Int): Square? {
        return try {
            val position = Position.from(file, rank)
            squares[position]
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun find(piece: Piece): Square? =
        squares.values.firstOrNull { it.piece == piece }

    /**
     * Finds all squares on the board that contain a piece of the specified type and color.
     *
     * @param T The type of piece to find (must be a subclass of [Piece]).
     * @param color The color of the piece to find.
     * @return A list of [Square] objects that contain a piece of type [T] and the specified [color].
     *         Returns an empty list if no matching pieces are found.
     *
     * @throws IllegalArgumentException if 'squares' is empty or null.
     *
     * @sample
     * // Assuming you have a board with some squares and pieces:
     * val board = Board() // some implementation of board
     * val whitePawns = board.find<Pawn>(Color.WHITE)
     * println("White pawns are on squares: ${whitePawns.joinToString { it.name }}")
     *
     * val blackQueens = board.find<Queen>(Color.BLACK)
     * println("Black queens are on squares: ${blackQueens.joinToString { it.name }}")
     *
     * val allPieces= board.find<Piece>(Color.WHITE) //this will return all white pieces of all types.
     * println("All white pieces are on squares: ${allPieces.joinToString { it.name }}")
     */
    inline fun <reified T : Piece> find(color: Color): List<Square> =
        squares.values.filter {
            it.piece != null &&
                    it.piece::class == T::class &&
                    it.piece.color == color
        }

    fun apply(effect: PieceEffect?): Board =
        effect?.applyOn(this) ?: this

    fun pieces(color: Color): Map<Position, Piece> =
        pieces.filter { (_, piece) -> piece.color == color }
}

/**
 * Represents the initial placement of chess pieces on the board at the start of a game.
 *
 * This map associates each square on the chessboard with the chess piece that occupies that square at the beginning of a standard game.
 *
 * The pieces are arranged as follows:
 * - **Black's back rank (rank 8):** Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
 * - **Black's pawns (rank 7):** Eight pawns
 * - **White's pawns (rank 2):** Eight pawns
 * - **White's back rank (rank 1):** Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
 *
 * Each entry in the map is a key-value pair:
 * - **Key:** A `Square` representing the square's location (e.g., a8, e1).
 * - **Value:** A `Piece` object representing the chess piece and its color (e.g., `Rook(BLACK)`, `Pawn(WHITE)`).
 *
 * This data structure is used to initialize the board's state when a new game is started.
 */
private val initialPieces = mapOf(
    a8 to Rook(BLACK),
    b8 to Knight(BLACK),
    c8 to Bishop(BLACK),
    d8 to Queen(BLACK),
    e8 to King(BLACK),
    f8 to Bishop(BLACK),
    g8 to Knight(BLACK),
    h8 to Rook(BLACK),

    a7 to Pawn(BLACK),
    b7 to Pawn(BLACK),
    c7 to Pawn(BLACK),
    d7 to Pawn(BLACK),
    e7 to Pawn(BLACK),
    f7 to Pawn(BLACK),
    g7 to Pawn(BLACK),
    h7 to Pawn(BLACK),

    a2 to Pawn(WHITE),
    b2 to Pawn(WHITE),
    c2 to Pawn(WHITE),
    d2 to Pawn(WHITE),
    e2 to Pawn(WHITE),
    f2 to Pawn(WHITE),
    g2 to Pawn(WHITE),
    h2 to Pawn(WHITE),

    a1 to Rook(WHITE),
    b1 to Knight(WHITE),
    c1 to Bishop(WHITE),
    d1 to Queen(WHITE),
    e1 to King(WHITE),
    f1 to Bishop(WHITE),
    g1 to Knight(WHITE),
    h1 to Rook(WHITE),
)