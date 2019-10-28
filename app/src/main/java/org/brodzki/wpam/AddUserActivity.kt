package org.brodzki.wpam

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_add_user.*
import org.brodzki.wpam.dao.DAO
import org.brodzki.wpam.dao.Role
import org.brodzki.wpam.dao.User

class AddUserActivity : SignedInActivity() {

    /**
     * LIFECYCLE HANDLERS
     */

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        loggedUser = intent.extras.getSerializable(LOGGED_USER) as User
        updateView()

        //set buttons handlers
        confirm_add_button.setOnClickListener { addUser() }
    }

    override fun onResume() {
        super.onResume()
        updateView()
    }

    private fun updateView() {
        add_user_username_input.setText("")
        add_user_password_input.setText("")
        add_user_password_input2.setText("")
        add_user_administrative_switch.isChecked = false
        add_user_administrative_switch.isClickable = loggedUser.Role == Role.ADMIN
        add_user_logs_textview.text = ""
    }

    /**
     * BUTTONS HANDLERS
     */

    @SuppressLint("SetTextI18n")
    private fun addUser() {
        val changedUser = loggedUser.copy()
        changedUser.Username = add_user_username_input.text
        changedUser.Password = add_user_password_input.text
        changedUser.Role = if (add_user_administrative_switch.isChecked) Role.ADMIN else Role.VOLUNTEER

        //check if new password match
        if(add_user_password_input.text.toString() != add_user_password_input2.text.toString()) {
            add_user_logs_textview.setTextColor(getColorStateList(R.color.colorError))
            add_user_logs_textview.text = "${getString(R.string.not_match_err)}!"
        }
        //if so, create user
        else {
            showProgress(true)
            DAO(this).createUser(changedUser, {

                showProgress(false)
                add_user_logs_textview.text = getString(R.string.user_created)
                add_user_logs_textview.setTextColor(getColorStateList(R.color.colorSuccess))

            },  {
                showProgress(false)
                add_user_logs_textview.text = getString(R.string.user_exists)
                add_user_logs_textview.setTextColor(getColorStateList(R.color.colorError))
            })
        }
    }

    /**
     * PROGRESS BAR
     */
    private fun showProgress(show: Boolean) {
        add_user_view.visibility = if (show) View.GONE else View.VISIBLE
        add_user_progress.visibility = if (show) View.VISIBLE else View.GONE
    }


}
