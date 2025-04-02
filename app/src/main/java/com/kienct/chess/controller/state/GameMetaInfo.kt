package com.kienct.chess.controller.state

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date

@Parcelize
data class GameMetaInfo(
    val tags: Map<String, String>,
) : Parcelable {

    operator fun get(key: String): String? =
        tags[key]

    @IgnoredOnParcel
    val white: String? = get(KEY_WHITE)

    @IgnoredOnParcel
    val black: String? = get(KEY_BLACK)

    companion object {
        private const val KEY_EVENT = "Event"
        private const val KEY_SITE = "Site"
        private const val KEY_DATE = "Date"
        const val KEY_WHITE = "White"
        const val KEY_BLACK = "Black"
        const val KEY_RESULT = "Result"
        const val KEY_TERMINATION = "Termination"

        @SuppressLint("SimpleDateFormat")
        fun createWithDefaults(): GameMetaInfo =
            GameMetaInfo(
                tags = mapOf(
                    KEY_EVENT to "Default event",
                    KEY_SITE to "Default site",
                    KEY_DATE to SimpleDateFormat("yyyy-M-dd").format(Date()),
                    KEY_WHITE to "Player 1",
                    KEY_BLACK to "Player 2",
                )
            )

    }
}