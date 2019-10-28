package org.brodzki.wpam

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.activity_generate_voters_db.*
import org.brodzki.wpam.dao.DAO
import org.brodzki.wpam.dao.User
import org.brodzki.wpam.dao.Voter

class GenerateVotersDbActivity : SignedInActivity() {

    /**
     * LIFECYCLE HANDLERS
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_voters_db)
        loggedUser = intent.extras.getSerializable(LOGGED_USER) as User

        generate_voters_button.setOnClickListener { onGenerateVoter() }

        updateView()
    }

    override fun onResume() {
        super.onResume()
        updateView()
    }

    private fun updateView() {
        voters_number_input.setText("")
        radius_input.setText("")
        generate_logs_textview.text = ""
    }

    /**
     * BUTTONS HANDLERS
     */

    @SuppressLint("SetTextI18n")
    private fun onGenerateVoter() {

        // check if numbers are valid
        var voters = -1
        var radius = -1
        try {
            voters = Integer.parseInt(voters_number_input.text.toString())
        } catch(e: NumberFormatException) { }
        try {
            radius = Integer.parseInt(radius_input.text.toString())
        } catch(s: NumberFormatException) { }
        if (!(voters > 0 && radius > 0)) {
            generate_logs_textview.setTextColor(getColor(R.color.colorError))
            generate_logs_textview.text = "${getString(R.string.nonnegative_integers)}!"
        }
        //if numbers are valid, generate voters
        else {
            // get current location as center of circle
            try {
                showProgress(true)
                progress_percent.text = "0.0%"

                val lastLocationTask = FusedLocationProviderClient(this).lastLocation
                lastLocationTask.addOnCompleteListener {

                    val center = it.result
                    var succeededQueries = 0
                    val totalQueries = voters * 2

                    fun updateProgressBar() {
                        succeededQueries++
                        progress_percent.text = "${Math.round( 1000.0 * succeededQueries / totalQueries ) / 10.0}%"
                        if(succeededQueries == totalQueries) {
                            showProgress(false)
                            generate_logs_textview.setTextColor(getColor(R.color.colorSuccess))
                            generate_logs_textview.text = "${getString(R.string.voters_generated)}!"
                        }
                    }

                    //when center given, generate voters
                    val dao = DAO(this)
                    for(i in 1..voters) {
                        Voter.random(this, center, radius, {
                            updateProgressBar()
                            dao.createVoter(it, {
                                updateProgressBar()
                            }, {
                                updateProgressBar()
                            })
                        })

                    }
                }
            }
            catch(s: SecurityException) {
                showSimpleDialog(DialogType.ERROR, "No required permissions!")
            }
            catch(e: Throwable) {
                showSimpleDialog(DialogType.ERROR, e.message.toString())
            }

        }
    }


    /**
     * PROGRESS BAR
     */

    private fun showProgress(show: Boolean) {
        generate_voters_view.visibility = if (show) View.GONE else View.VISIBLE
        generate_voters_progress.visibility = if (show) View.VISIBLE else View.GONE
        progress_percent.visibility = generate_voters_progress.visibility
    }



}
