package org.brodzki.wpam

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_edit_user.*
import org.brodzki.wpam.dao.DAO
import org.brodzki.wpam.dao.Role
import org.brodzki.wpam.dao.User
import java.io.Serializable

class EditUserActivity : SignedInActivity() {

    /**
     * GLOBAL STATE
     */
    private lateinit var editedUser: User

    /**
     * LIFECYCLE HANDLERS
     */

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        loggedUser = intent.extras.getSerializable(LOGGED_USER) as User
        editedUser = loggedUser.copy()
        updateView()

        //set buttons handlers
        apply_button.setOnClickListener { onApplySettings() }
        administrative_switch.setOnClickListener { onSwitchToggled() }
        delete_user_button.setOnClickListener { onDeleteUser() }
        add_user_button.setOnClickListener { onAddUser() }
    }

    override fun onResume() {
        super.onResume()
        loggedUser = intent.extras.getSerializable(LOGGED_USER) as User
        editedUser = loggedUser.copy()
        updateView()
    }

    private fun updateView() {
        textView.text = loggedUser.Username
        username_input.setText(editedUser.Username)
        password_input.setText("")
        password_input2.setText("")
        administrative_switch.isChecked = editedUser.Role == Role.ADMIN
        administrative_switch.isClickable = editedUser.Role == Role.ADMIN
        apply_logs_textview.text = ""
    }

    /**
     * BUTTONS HANDLERS
     */

    private fun onSwitchToggled() {
        if(loggedUser.Role == Role.ADMIN && !administrative_switch.isChecked) {
            showSimpleDialog(DialogType.WARNING, "${getString(R.string.administrative_warning)}!")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onApplySettings() {
        val changedUser = editedUser.copy()
        changedUser.Username = username_input.text
        changedUser.Password = password_input.text
        changedUser.Role = if (administrative_switch.isChecked) Role.ADMIN else Role.VOLUNTEER

        //check if new password match
        if(password_input.text.toString() != password_input2.text.toString()) {
            apply_logs_textview.setTextColor(getColorStateList(R.color.colorError))
            apply_logs_textview.text = "${getString(R.string.not_match_err)}!"
        }
        //if so, update user
        else {
            showProgress(true)
            DAO(this).updateUser(editedUser.Username, changedUser, {
                //update password if necessary
                if (password_input.text.toString() != "") {
                    DAO(this).updateUserPassword(changedUser.Username, password_input.text, {

                        if(editedUser == loggedUser) {
                            loggedUser = changedUser
                        }
                        editedUser = changedUser.copy()
                        updateView()
                        showProgress(false)
                        apply_logs_textview.text = getString(R.string.settings_applied)
                        apply_logs_textview.setTextColor(getColorStateList(R.color.colorSuccess))

                    }, {
                        showProgress(false)
                        showSimpleDialog(DialogType.ERROR, it.message.toString())
                    })
                }
                //if not, just update username and role
                else {
                    if(editedUser == loggedUser) {
                        loggedUser = changedUser
                    }
                    editedUser = changedUser
                    updateView()
                    showProgress(false)
                    apply_logs_textview.text = getString(R.string.settings_applied)
                    apply_logs_textview.setTextColor(getColorStateList(R.color.colorSuccess))
                }
            },  {
                showProgress(false)
                showSimpleDialog(DialogType.ERROR, it.message.toString())
            })
        }
    }

    private fun onDeleteUser() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(DialogType.WARNING.toString())
        builder.setMessage("${getString(R.string.delete_confirmation)}!")
        builder.setPositiveButton("${getString(R.string.yes_delete)}!", { _, _ ->

            DAO(this).deleteUser(editedUser, {

                finish()
                startActivity(Intent(this, LoginActivity::class.java))

            }, {
                showProgress(false)
                showSimpleDialog(DialogType.ERROR, it.message.toString())
            })

        })
        builder.setNegativeButton(getString(R.string.cancel), { _, _ ->

        })
        builder.show()

    }

    private fun onAddUser() {
        val intent = Intent(this, AddUserActivity::class.java).apply {
            putExtra(LOGGED_USER, loggedUser as Serializable)
        }
        startActivity(intent)
    }

    /**
     * PROGRESS BAR
     */
    private fun showProgress(show: Boolean) {
        edit_user_view.visibility = if (show) View.GONE else View.VISIBLE
        edit_user_progress.visibility = if (show) View.VISIBLE else View.GONE
    }


}
