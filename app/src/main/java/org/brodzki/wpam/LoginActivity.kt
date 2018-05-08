package org.brodzki.wpam

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Bundle
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import org.brodzki.wpam.dao.DAO
import java.io.Serializable

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : VoteForUsActivity() {

    /**
     * GLOBAL STATE
     */
    private var rememberedUsername: CharSequence = ""
    private var rememberedPassword: CharSequence = ""

    /**
     * LIFECYCLE HANDLERS
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        updateView()

        //setting events handlers
        email_sign_in_button.setOnClickListener { attemptLogin() }

        //save default settings, of not set
        val sharedEditor = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit()
        if(!getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).contains(DEFAULT_DATA_SOURCE)) {
            sharedEditor.putString(DEFAULT_DATA_SOURCE, DEFAULT_DATA_SOURCE_VALUE)
        }
        if(!getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).contains(DEFAULT_LOW_PROXIMITY)) {
            sharedEditor.putFloat(DEFAULT_LOW_PROXIMITY, DEFAULT_LOW_PROXIMITY_VALUE)
        }
        if(!getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).contains(DEFAULT_HIGH_PROXIMITY)) {
            sharedEditor.putFloat(DEFAULT_HIGH_PROXIMITY, DEFAULT_HIGH_PROXIMITY_VALUE)
        }
        sharedEditor.apply()
    }

    override fun onResume() {
        super.onResume()
        updateView()
    }

    private fun updateView() {
        rememberedUsername = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(REMEMBERED_USERNAME, "")
        rememberedPassword = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(REMEMBERED_PASSWORD, "")
        username.setText(rememberedUsername)
        password.setText(rememberedPassword)
        remeber_login_checkbox.isChecked = rememberedUsername != ""
    }

    /**
     * MENU HANDLERS
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        this.menuInflater.inflate(R.menu.login_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        when(item!!.itemId) {

            R.id.action_back -> {
                //just close the menu
            }

            R.id.action_close -> {
                finish()
            }
        }
        return true
    }

    /**
     * PERMISSION HANDLER
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (! (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            val errorDialog = AlertDialog.Builder(this)
            errorDialog.setTitle(R.string.permissions_erorr_dialog_title)
            errorDialog.setMessage(R.string.permissions_error_dialog_message)
            errorDialog.setPositiveButton(R.string.ok, { _, _ -> })
        }
    }

    /**
     * BUTTONS HANDLERS
     */

    private fun attemptLogin() {

        showProgress(true)
        DAO(this).checkCredentials(username.text, password.text, {

            val sharedEditor = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit()
            if(remeber_login_checkbox.isChecked) {
                sharedEditor.putString(REMEMBERED_USERNAME, username.text.toString())
                sharedEditor.putString(REMEMBERED_PASSWORD, password.text.toString())
            }
            else {
                sharedEditor.putString(REMEMBERED_USERNAME, "")
                sharedEditor.putString(REMEMBERED_PASSWORD, "")
            }
            sharedEditor.apply()

            DAO(this).readUser("Username = '${username.text}'", {
                val intent = Intent(this, DashboardActivity::class.java).apply {
                    putExtra(LOGGED_USER, it[0] as Serializable)
                }
                startActivity(intent)

                login_logs_textview.text = ""
                showProgress(false)


            }, {
                showSimpleDialog(DialogType.ERROR, it.message.toString())
            })


        }, {
            showProgress(false)
            login_logs_textview.text = getString(R.string.invalid_credentials)
        } )

    }

    /**
     * PROGRESS BAR
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        login_view.visibility = if (show) View.GONE else View.VISIBLE
        login_progress.visibility = if (show) View.VISIBLE else View.GONE
    }

}
