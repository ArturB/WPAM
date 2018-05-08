package org.brodzki.wpam

import android.annotation.SuppressLint
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_propaganda.*
import org.brodzki.wpam.dao.*

class PropagandaActivity : SignedInActivity() {

    private lateinit var voter: Voter
    private lateinit var rule: Rule

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_propaganda)
        loggedUser = intent.extras.getSerializable(LOGGED_USER) as User

        voter = intent.extras.getSerializable(SELECTED_VOTER) as Voter
        rule = intent.extras.getSerializable(SELECTED_RULE) as Rule

        name_textview.text              = "${voter.Name} ${voter.Surname}, ${voter.Age} ${getString(R.string.years_old)}"
        openness_textview.text          = "${getString(R.string.openness_feature)}: ${featureName(voter.Openness)}"
        conscientiousness_textview.text = "${getString(R.string.conscientiousness_feature)}: ${featureName(voter.Conscientiousness)}"
        extraversion_textview.text      = "${getString(R.string.extraversion_feature)}: ${featureName(voter.Extraversion)}"
        agreeableness_textview.text     = "${getString(R.string.agreeableness_feature)}: ${featureName(voter.Agreeableness)}"
        neuroticism_textview.text       = "${getString(R.string.neuroticism_feature)}: ${featureName(voter.Neuroticism)}"
        leadership_textview.text        = "${getString(R.string.leadership_feature)}: ${featureName(voter.Leadership)}"
        orientation_textview.text       = "${getString(R.string.orientation_feature)}: ${orientationCode(voter.Orientation)}"
        affiliation_textview.text       = "${getString(R.string.affiliation_feature)}: ${voter.PoliticalAffiliation}"
        summary_textview.text           = rule.Text
        proximity_text_view.text        = "${getString(R.string.proximity_title)}: ${Math.floor(100 - MarkerData.voterRuleDistance(voter, rule))}%"

    }

    private fun featureName(f: Int): String {
        return when {
            f > 0 -> getString(R.string.high)
            f == 0 -> getString(R.string.medium)
            else -> getString(R.string.low)
        }
    }

    private fun orientationCode(o: Orientation?): String {
        return when (o) {
            Orientation.HETEROSEXUAL -> getString(R.string.heterosexual)
            Orientation.HOMOSEXUAL -> getString(R.string.homosexual)
            Orientation.BISEXUAL -> getString(R.string.bisexual)
            Orientation.ASEXUAL -> getString(R.string.asexual)
            else -> getString(R.string.unknown)
        }
    }
}
