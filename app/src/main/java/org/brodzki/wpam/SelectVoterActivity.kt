package org.brodzki.wpam

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_select_voter.*
import org.brodzki.wpam.dao.*

class SelectVoterActivity : SignedInActivity() {

    lateinit var voters: ArrayList<Voter>
    lateinit var rules: ArrayList<Rule>
    lateinit var votersAdapter: VotersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_voter)
        loggedUser = intent.extras.getSerializable(LOGGED_USER) as User
        voters = intent.extras.getSerializable(SHARED_VOTERS) as ArrayList<Voter>
        rules = intent.extras.getSerializable(RULES) as ArrayList<Rule>
        select_voter_recyclerview.adapter = VotersAdapter(voters, rules, loggedUser)
        select_voter_recyclerview.layoutManager = LinearLayoutManager(this)

    }




}
