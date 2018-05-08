package org.brodzki.wpam.dao

import java.io.Serializable

enum class Role : Serializable {
    ADMIN, VOLUNTEER, UNKNOWN;

    override fun toString(): String {
        return when (this) {
            ADMIN -> "admin"
            VOLUNTEER -> "volunteer"
            UNKNOWN -> "unknown"
        }
    }

    companion object {
        fun fromString(s: String): Role {
            return when (s) {
                "admin" -> Role.ADMIN
                "volunteer" -> Role.VOLUNTEER
                else -> Role.UNKNOWN
            }
        }
    }



}