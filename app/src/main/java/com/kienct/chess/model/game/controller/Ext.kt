package com.kienct.chess.model.game.controller

import com.kienct.chess.model.game.Resolution
import com.kienct.chess.model.game.state.GameMetaInfo
import com.kienct.chess.model.piece.Color

/**
 * Represents the resolution of a game.
 */
fun GameMetaInfo.withResolution(resolution: Resolution, lastMoveBy: Color): GameMetaInfo =
    when (resolution) {
        Resolution.IN_PROGRESS -> this
        Resolution.CHECKMATE -> {
            val result = if (lastMoveBy == Color.WHITE) "1-0" else "0-1"
            val winner = if (lastMoveBy == Color.WHITE) white else black
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to result)
                    .plus(GameMetaInfo.KEY_TERMINATION to "$winner won by checkmate")
            )
        }
        Resolution.STALEMATE -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "½ - ½")
                    .plus(GameMetaInfo.KEY_TERMINATION to "Stalemate")
            )
        }
        Resolution.DRAW_BY_REPETITION -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "½ - ½")
                    .plus(GameMetaInfo.KEY_TERMINATION to "Draw by repetition")
            )
        }
        Resolution.INSUFFICIENT_MATERIAL -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "½ - ½")
                    .plus(GameMetaInfo.KEY_TERMINATION to "Draw by insufficient material")
            )
        }
    }
