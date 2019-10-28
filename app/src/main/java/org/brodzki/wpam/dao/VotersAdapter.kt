package org.brodzki.wpam.dao

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import org.brodzki.wpam.PropagandaActivity
import org.brodzki.wpam.R
import org.brodzki.wpam.VoteForUsActivity

class VotersAdapter(
        private val voters: ArrayList<Voter>,
        private val rules: ArrayList<Rule>,
        private val loggedUser: User
) : RecyclerView.Adapter<VotersAdapter.ViewHolder>() {

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    private var lowThreshold = VoteForUsActivity.DEFAULT_LOW_PROXIMITY_VALUE
    private var highThreshold = VoteForUsActivity.DEFAULT_HIGH_PROXIMITY_VALUE
    private var lowProximityColor = -1
    private var mediumProximityColor = -1
    private var highProximityColor = -1

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): VotersAdapter.ViewHolder {

        lowThreshold = parent.context.getSharedPreferences(
                VoteForUsActivity.SHARED_PREFS, Context.MODE_PRIVATE).
                getFloat(VoteForUsActivity.DEFAULT_LOW_PROXIMITY, VoteForUsActivity.DEFAULT_LOW_PROXIMITY_VALUE)
        highThreshold = parent.context.getSharedPreferences(
                VoteForUsActivity.SHARED_PREFS, Context.MODE_PRIVATE).
                getFloat(VoteForUsActivity.DEFAULT_HIGH_PROXIMITY, VoteForUsActivity.DEFAULT_HIGH_PROXIMITY_VALUE)
        lowProximityColor = parent.context.getColor(R.color.colorError)
        mediumProximityColor = parent.context.getColor(R.color.colorMedium)
        highProximityColor = parent.context.getColor(R.color.colorSuccess)

        val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.sample_voter_text_view, parent, false) as TextView
        textView.setOnClickListener {
            if( voters[it.id].id != 0) {
                val intent = Intent(parent.context, PropagandaActivity::class.java).apply {
                    putExtra(VoteForUsActivity.LOGGED_USER, loggedUser)
                    putExtra(VoteForUsActivity.SELECTED_VOTER, voters[textView.id])
                    putExtra(VoteForUsActivity.SELECTED_RULE, rules[textView.id])
                }
                parent.context.startActivity(intent)
            }
        }
        return ViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.text = "${voters[position].Name} ${voters[position].Surname}, ${voters[position].Age}"
        val proximity = 100 - MarkerData.voterRuleDistance(voters[position], rules[position])
        holder.textView.setTextColor(when {
            proximity < lowThreshold -> lowProximityColor
            proximity < highThreshold -> mediumProximityColor
            else -> highProximityColor
        })
        holder.textView.id = position
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = voters.size
}