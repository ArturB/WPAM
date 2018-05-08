package org.brodzki.wpam.dao

import java.io.Serializable

data class MarkerData(
        val address: String,
        val shortAddress: String,
        val latitude: Double,
        val longitude: Double,
        var cosDistance: Double
) : Serializable {

    var voters = ArrayList<Voter>()
    var rules  = ArrayList<Rule>()

    companion object {

        fun voterRuleDistance(v: Voter, r: Rule): Double {

            val voterVector = arrayOf(
                            1.0 * v.Openness,
                            1.0 * v.Conscientiousness,
                            1.0 * v.Extraversion,
                            1.0 * v.Agreeableness,
                            1.0 * v.Neuroticism,
                            1.0 * v.Leadership,
                            if (v.Age in 18..24) 1.0 else 0.0,
                            if (v.Age in 25..34) 1.0 else 0.0,
                            if (v.Age in 35..44) 1.0 else 0.0,
                            if (v.Age in 45..64) 1.0 else 0.0,
                            if (v.Age > 65) 1.0 else 0.0,
                            if (v.Orientation == Orientation.HETEROSEXUAL) 1.0 else 0.0,
                            if (v.Orientation == Orientation.HOMOSEXUAL) 1.0 else 0.0,
                            if (v.Orientation == Orientation.BISEXUAL) 1.0 else 0.0,
                            if (v.Orientation == Orientation.ASEXUAL) 1.0 else 0.0
            )

            val ruleVector = arrayOf(
                    1.0 * r.Openness,
                    1.0 * r.Conscientiousness,
                    1.0 * r.Extraversion,
                    1.0 * r.Agreeableness,
                    1.0 * r.Neuroticism,
                    1.0 * r.Leadership,
                    1.0 * r.Age18to24,
                    1.0 * r.Age25to34,
                    1.0 * r.Age35to44,
                    1.0 * r.Age45to64,
                    1.0 * r.Age65,
                    1.0 * r.OrientationHeterosexual,
                    1.0 * r.OrientationHeterosexual,
                    1.0 * r.OrientationBisexual,
                    1.0 * r.OrientationAsexual
            )

            fun length(v: Array<Double>): Double {
                var result = 0.0
                for(x in v) {
                    result += Math.pow(x, 2.0)
                }
                return Math.sqrt(result)
            }

            return if (length(voterVector) < 0.5 || length(ruleVector) < 0.5) {
                Math.round(Math.toDegrees(Math.acos(1.0))) * 100.0 / 90.0
            }
            else {
                var result = 0.0
                for(i in 0 until voterVector.size) {
                    result += voterVector[i] * ruleVector[i]
                }
                val cos = result / ( length(voterVector) * length(ruleVector) )
                Math.round(Math.toDegrees(Math.acos(if (cos <= 1.0) cos else 1.0))) * 100.0 / 90.0
            }
        }

        fun fromVotersList(list: Array<Voter>, rules: Array<Rule>): ArrayList<MarkerData> {
            val result = ArrayList<MarkerData>()

            fun findByAddress(v: Voter): MarkerData? {
                for(existingMarker in result) {
                    if(existingMarker.address == v.Address) {
                        return existingMarker
                    }
                }
                return null
            }

            fun findBestRule(v: Voter): Rule {
                var bestDistance = voterRuleDistance(v, rules[0])
                var bestRule = rules[0]
                for(r in rules) {
                    if(voterRuleDistance(v, r) < bestDistance) {
                        bestDistance = voterRuleDistance(v, r)
                        bestRule = r
                    }
                }
                return bestRule
            }

            for(voter in list) {
                val f = findByAddress(voter)
                if(f == null) {
                    val newMarker = MarkerData(
                            voter.Address.toString(),
                            voter.Address.split(",")[0],
                            voter.Latitude,
                            voter.Longitude,
                            voterRuleDistance(voter, findBestRule(voter))
                    )
                    newMarker.voters.add(voter)
                    newMarker.rules.add(findBestRule(voter))
                    result.add(newMarker)
                }
                else {
                    val bestDistance = voterRuleDistance(voter, findBestRule(voter))
                    f.cosDistance = if ( bestDistance < f.cosDistance ) bestDistance else f.cosDistance
                    f.voters.add(voter)
                    f.rules.add(findBestRule(voter))

                }
            }
            return result
        }
    }


}