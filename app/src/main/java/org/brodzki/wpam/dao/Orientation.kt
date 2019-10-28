package org.brodzki.wpam.dao

import java.io.Serializable

enum class Orientation : Serializable {

    HETEROSEXUAL, HOMOSEXUAL, BISEXUAL, ASEXUAL, UNKNOWN;

    override fun toString(): String {
        return when(this) {
            HETEROSEXUAL -> "heterosexual"
            HOMOSEXUAL -> "homosexual"
            BISEXUAL -> "bisexual"
            ASEXUAL -> "asexual"
            UNKNOWN -> "unknown"
        }
    }

    companion object {
        fun fromString(s: String): Orientation {
            return when (s) {
                "heterosexual" -> Orientation.HETEROSEXUAL
                "homosexual" -> Orientation.HOMOSEXUAL
                "bisexual" -> Orientation.BISEXUAL
                "asexual" -> Orientation.ASEXUAL
                else -> Orientation.UNKNOWN
            }
        }
    }



}