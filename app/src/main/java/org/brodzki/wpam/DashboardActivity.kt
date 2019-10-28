package org.brodzki.wpam

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.brodzki.wpam.dao.Role
import org.brodzki.wpam.dao.User

class DashboardActivity : SignedInActivity() {

    /**
     * LIFECYCLE HANDLERS
     */

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        loggedUser = intent.extras.getSerializable(LOGGED_USER) as User
        updateView()

        //set events handlers
        manage_users_button.setOnClickListener { gotoGenerateVoters() }
        show_maps_button.setOnClickListener { gotoMaps() }
    }

    override fun onResume() {
        super.onResume()
        //loggedUser = intent.extras.getSerializable(LOGGED_USER) as User
        updateView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MapsActivity.LOCATION_REQUEST) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showSimpleDialog(DialogType.ERROR, getString(R.string.permissions_error_dialog_message))
            }
            else {
                val intent = Intent(this, MapsActivity::class.java).apply {
                    putExtra(LOGGED_USER, loggedUser)
                }
                startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateView() {
        hello_texview.text = "${getString(R.string.hello_text)}, ${loggedUser.Username}!"
        last_login_textview.text = "${getString(R.string.last_login)}: ${loggedUser.LastLogin} UTC"
        role_textview.text = "${getString(R.string.role_text)}: ${loggedUser.Role}"
    }

    /**
     * BUTTONS HANDLERS
     */

    private fun gotoGenerateVoters() {
        if(loggedUser.Role == Role.ADMIN) {
            val intent = Intent(this, GenerateVotersDbActivity::class.java).apply {
                putExtra(LOGGED_USER, loggedUser)
            }
            startActivity(intent)
        }
        else {
            showSimpleDialog(DialogType.ERROR, "${getString(R.string.generate_permissions)}!")
        }
    }

    private fun gotoMaps() {
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MapsActivity.LOCATION_REQUEST)
        }
        else {
            val intent = Intent(this, MapsActivity::class.java).apply {
                putExtra(LOGGED_USER, loggedUser)
            }
            startActivity(intent)
        }

    }

    /**
     * PROGRESS BAR
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        dashboard_view.visibility = if (show) View.GONE else View.VISIBLE
        dashboard_progress.visibility = if (show) View.VISIBLE else View.GONE
    }



}
