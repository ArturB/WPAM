package org.brodzki.wpam.dao

import java.io.Serializable
import java.net.URLEncoder

data class Rule (
        var id: Int,
        var Text: CharSequence,
        var Openness: Int,
        var Conscientiousness: Int,
        var Extraversion: Int,
        var Agreeableness: Int,
        var Neuroticism: Int,
        var Leadership: Int,
        var Age18to24: Int,
        var Age25to34: Int,
        var Age35to44: Int,
        var Age45to64: Int,
        var Age65: Int,
        var GenderFemale: Int,
        var GenderMale: Int,
        var GenderOther: Int,
        var OrientationHeterosexual: Int,
        var OrientationHomosexual: Int,
        var OrientationBisexual: Int,
        var OrientationAsexual: Int
) : Serializable {

    val utf = "UTF-8"

    fun encode(): Rule {
        val result = this
        result.Text = URLEncoder.encode(Text.toString(), utf)
        return result
    }

}
