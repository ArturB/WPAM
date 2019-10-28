package org.brodzki.wpam.dao

import java.io.Serializable

enum class Gender : Serializable {
    MALE, FEMALE, OTHER;

    override fun toString(): String {
        return when (this) {
            MALE -> "male"
            FEMALE -> "female"
            OTHER -> "other"
        }
    }

    companion object {
        fun fromString(s: String): Gender {
            return when (s) {
                "male" -> Gender.MALE
                "female" -> Gender.FEMALE
                else -> Gender.OTHER
            }
        }
    }


}