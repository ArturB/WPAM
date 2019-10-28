package org.brodzki.wpam.dao

import java.io.Serializable
import java.net.URLEncoder

data class User (
        var id: Int,
        var Username: CharSequence,
        var Password: CharSequence?,
        var LastLogin: String,
        var Role: Role
) : Serializable {

    val utf = "UTF-8"

    fun encode(): User {
        val result = this
        result.Username = URLEncoder.encode(Username.toString(), utf)
        result.Password = URLEncoder.encode(Password.toString(), utf)
        return result
    }

    override fun toString(): String {
        return Username.toString() + ", " + Role
    }

}
