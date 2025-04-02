package com.kienct.chess.controller.state

import android.os.Parcelable
import com.kienct.chess.model.board.Board
import com.kienct.chess.model.board.Position
import com.kienct.chess.controller.Resolution
import com.kienct.chess.model.move.AppliedMove
import com.kienct.chess.model.move.BoardMove
import com.kienct.chess.model.move.Capture
import com.kienct.chess.model.move.MoveEffect
import com.kienct.chess.model.move.targetPositions
import com.kienct.chess.model.piece.Color
import com.kienct.chess.model.piece.King
import com.kienct.chess.model.piece.Piece
import kotlinx.parcelize.Parcelize

/**
 * Represents a snapshot of the game state at a particular moment.
 *
 * This data class encapsulates all the necessary information to fully describe
 * the state of a chess game, including the board configuration, whose turn it is,
 * the game's resolution (e.g., in progress, checkmate), the last move made, castling
 * rights, and captured pieces.
 *
 * @property board The current configuration of the chessboard.  Defaults to a standard starting position.
 * @property toMove The color (WHITE or BLACK) of the player whose turn it is. Defaults to WHITE.
 * @property resolution The current resolution of the game (e.g., IN_PROGRESS, CHECKMATE, STALEMATE).  Defaults to IN_PROGRESS.
 * @property move The move currently being considered or executed, if any.  Defaults to null.
 * @property lastMove The last move that was made in the game, if any. Defaults to null.
 * @property castlingInfo Information about castling rights for both players.  Defaults to initial castling rights.
 * @property capturedPieces A list of pieces that have been captured during the game. Defaults to an empty list.
 */
@Parcelize
data class GameSnapshotState(
    val board: Board = Board(),
    val toMove: Color = Color.WHITE,
    val resolution: Resolution = Resolution.IN_PROGRESS,
    val move: AppliedMove? = null,
    val lastMove: AppliedMove? = null,
    val castlingInfo: CastlingInfo = CastlingInfo.from(board),
    val capturedPieces: List<Piece> = emptyList()
) : Parcelable {

    fun hasCheck() = hasCheckFor(toMove)

    /**
     * Checks if the given color is in check.
     *
     * This function determines if the king of the specified color is under attack
     * (in "check") by any opponent's pieces on the board. It first locates the king's
     * position and then calls another `hasCheckFor` function to check if that
     * position is under attack.
     *
     * @param color The color of the king to check for check.
     * @return `true` if the king of the specified color is in check, `false` otherwise.
     *         Returns `false` if there is no king of the given color on the board.
     */
    private fun hasCheckFor(color: Color): Boolean {
        val kingsPosition: Position =
            board.find<King>(color).firstOrNull()?.position ?: return false

        return hasCheckFor(kingsPosition)
    }

    /**
     * Checks if any piece on the board has a pseudo-legal capture move that targets the given position.
     *
     * This function iterates through all pieces on the board and checks their pseudo-legal capture
     * moves (moves that are legal according to the piece's movement rules, but may leave the king
     * in check). If any of these moves target the specified position, it means a piece *could*
     * capture a piece at that position (ignoring the possibility of check).
     *
     * @param position The [Position] to check if any piece has a capture move for.
     * @return `true` if any piece has a pseudo-legal capture move targeting the given position, `false` otherwise.
     */
    fun hasCheckFor(position: Position): Boolean =
        board.pieces.any { (_, piece) ->
            val otherPieceCaptures: List<BoardMove> = piece.pseudoLegalMoves(this, true)
                .filter { it.preMove is Capture }
            position in otherPieceCaptures.targetPositions()
        }


    /**
     * Calculates the result of applying a given move to the current game state.
     *
     * This function determines the immediate effects of a move, such as check, checkmate, or stalemate,
     * and updates the game state accordingly. It also checks for draw conditions like threefold repetition
     * and insufficient material.
     *
     * @param move The `BoardMove` to apply to the current game state. This represents a single move made by a player.
     * @param previousStates A list of `GameSnapshotState` representing the history of game states leading up to the current turn.
     *                       This is used to detect threefold repetition.
     * @return A `GameStateTransition` object containing:
     *   - `move`: The `AppliedMove` that was made, along with any effect it caused (e.g., CHECK, CHECKMATE).
     *   - `fromSnapshotState`: The `GameSnapshotState` before the move was applied, modified to include the applied move.
     *   - `toSnapshotState`: The `GameSnapshotState` after the move was applied, including updates to the board,
     *                        game resolution, last move made, castling rights, and captured pieces.
     *
     * The function performs the following steps:
     * 1. **Derive Pseudo Game State**: Creates a new game state (`nextGameState`) based on the current state,
     *    applying the given `move`. This represents the board configuration after the move, but before checking
     *    for move legality, check, mate, etc.
     * 2. **Calculate Possible Moves**: Determines the possible moves for the player whose turn it is in the `nextGameState`.
     *    This is achieved by checking all pieces of the current player and if they have any legal move.
     * 3. **Check for Check**: Determines if the move places the opposing player's king in check.
     * 4. **Check for Checkmate/Stalemate**: If a check occurred:
     *    - If there are no possible moves for the checked player -> Checkmate.
     *    - If there are possible moves for the checked player -> Check, but not checkmate.
     *    - If no check and no possible moves -> Stalemate.
     * 5. **Check for Draw Conditions**: Checks for other draw conditions:
     *    - Insufficient Material */
    fun calculateAppliedMove(
        move: BoardMove,
        previousStates: List<GameSnapshotState>
    ): GameStateTransition {
        val nextGameState = derivePseudoGameState(move)
        val possibleMoves = with(nextGameState) {
            board.pieces(toMove).filter { (position, _) ->
                nextGameState.legalMovesFrom(position).isNotEmpty()
            }
        }
        val causesCheck = nextGameState.hasCheck()
        val causesCheckButNotMate = possibleMoves.isNotEmpty() && causesCheck
        val causesCheckMate = possibleMoves.isEmpty() && causesCheck
        val causesStalemate = possibleMoves.isEmpty() && !causesCheck
        val insufficientMaterialToContinue = nextGameState.board.pieces.hasInsufficientMaterial()
        val threeFoldRepetitionDetected = (previousStates + nextGameState).hasThreefoldRepetition()

        val moveEffect = when {
            causesCheckButNotMate -> MoveEffect.CHECK
            causesCheckMate -> MoveEffect.CHECKMATE
            causesStalemate -> MoveEffect.DRAW
            insufficientMaterialToContinue -> MoveEffect.DRAW
            threeFoldRepetitionDetected -> MoveEffect.DRAW
            else -> null
        }

        val applied = AppliedMove(
            boardMove = move.applyAmbiguity(this),
            effect = moveEffect,
        )

        val resolution = when {
            causesCheckMate -> Resolution.CHECKMATE
            causesStalemate -> Resolution.STALEMATE
            threeFoldRepetitionDetected -> Resolution.DRAW_BY_REPETITION
            insufficientMaterialToContinue -> Resolution.INSUFFICIENT_MATERIAL
            else -> Resolution.IN_PROGRESS
        }

        return GameStateTransition(
            move = applied,
            fromSnapshotState = this.copy(move = applied),
            toSnapshotState = nextGameState.copy(
                resolution = resolution,
                move = null,
                lastMove = applied,
                castlingInfo = castlingInfo.apply(move),
                capturedPieces = (move.preMove as? Capture)?.let { capturedPieces + it.piece }
                    ?: capturedPieces
            )
        )
    }

    /**
     * Calculates the legal moves for a piece at a given position on the board.
     *
     * This function determines the valid moves a piece can make from a specified position.
     * It first retrieves the piece at the given position. If no piece exists at that position,
     * it returns an empty list.
     * If a piece exists, it calculates the "pseudo-legal" moves for that piece (moves that are
     * legal according to the piece's movement rules, but might leave the king in check).
     * Then, it applies check constraints to these pseudo-legal moves, filtering out any moves that
     * would result in the current player's king being in check.
     *
     * @param position The position on the board from which to calculate legal moves.
     * @return A list of [BoardMove] objects representing the legal moves for the piece at the given position.
     *         Returns an empty list if there is no piece at the given position, or if no moves are legal after check constraints.
     */
    fun legalMovesFrom(position: Position): List<BoardMove> =
        board[position]
            .piece
            ?.pseudoLegalMoves(this, false)
            ?.applyCheckConstraints()
            ?: emptyList()

    /**
     * Applies check constraints to a list of board moves.
     *
     * This function filters a list of potential `BoardMove` objects, removing any move that would
     * result in the moving player's king being in check.  It ensures that any move made
     * is legal with respect to check constraints.
     *
     * @receiver A `List<BoardMove>` representing the potential moves to be filtered.
     * @return A `List<BoardMove>` containing only the moves that do not place the moving
     *         player's king in check.
     */
    private fun List<BoardMove>.applyCheckConstraints(): List<BoardMove> =
        filter { move ->
            // Any move made should result in no check (clear current if any, and not cause a new one)
            val newGameState = derivePseudoGameState(move)
            !newGameState.hasCheckFor(move.piece.color)
        }

    /**
     * Derives a pseudo-game state based on a given board move.
     *
     * This function simulates the application of a `BoardMove` to the current game state,
     * resulting in a new `GameSnapshotState` that reflects the hypothetical outcome of that move.
     * It essentially predicts the game state if the specified move sequence were to be executed.
     *
     * The function performs the following steps:
     * 1. **Applies the `preMove`, `move`, and `consequence` components of the `BoardMove` to the current `board`:**
     *    This creates an `updatedBoard` that represents the board's state after the move sequence.
     * 2. **Switches the `toMove` player:** The function changes the current player to the opposite player,
     *    indicating that the move has been made and it's the other player's turn.
     * 3. **Resets the `move` property:** Since this is a hypothetical state after a move, the `move` property,
     *    which might have held the move that was just made or is currently being considered, is set to `null`.
     * 4. **Updates the `lastMove` property:**  It sets the `lastMove` property to reflect the `BoardMove` that was just simulated,
     *    along with a `null` effect as we are only deriving the state, not applying an actual result.
     *
     * @param boardMove The `BoardMove` to simulate.
     * @return A new `GameSnapshotState` representing the pseudo-game state after the move.
     */
    private fun derivePseudoGameState(boardMove: BoardMove): GameSnapshotState {
        val updatedBoard = board
            .apply(boardMove.preMove)
            .apply(boardMove.move)
            .apply(boardMove.consequence)
        return copy(
            board = updatedBoard,
            toMove = toMove.opposite(),
            move = null,
            lastMove = AppliedMove(
                boardMove = boardMove,
                effect = null
            )
        )
    }

    fun toRepetitionRelevantState(): RepetitionRelevantState =
        RepetitionRelevantState(
            board = board,
            toMove = toMove,
            castlingInfo = castlingInfo
        )
}