package org.brodzki.wpam

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity

open class VoteForUsActivity : AppCompatActivity() {

    fun showSimpleDialog(type: DialogType, msg: String) {

        val builder = AlertDialog.Builder(this)
        val title = when (type) {
            DialogType.WARNING -> getString(R.string.warning_title)
            DialogType.ERROR -> getString(R.string.error_title)
            DialogType.INFO -> getString(R.string.info)
        }
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton("OK", { _, _ -> })
        builder.show()
    }

    companion object {

        const val LOGGED_USER = "logged_user"
        const val REMEMBERED_USERNAME = "remembered_username"
        const val REMEMBERED_PASSWORD = "remembered_password"
        const val SHARED_PREFS = "vote_for_us"
        const val SHARED_VOTERS = "shared_voters"
        const val SELECTED_VOTER = "selected_voter"
        const val RULES = "rules"
        const val SELECTED_RULE = "selected_rule"
        const val DEFAULT_DATA_SOURCE = "data_source"
        const val DEFAULT_LOW_PROXIMITY = "low_proximity"
        const val DEFAULT_HIGH_PROXIMITY = "high_proximity"
        const val DEFAULT_DATA_SOURCE_VALUE = "http://s2.brodzki.org"
        const val DEFAULT_LOW_PROXIMITY_VALUE = 25.0f
        const val DEFAULT_HIGH_PROXIMITY_VALUE = 50.0f

        //USED TO PRINT SHORT MESSAGE INSTEAD OF System.out.println
        fun showSimpleDialog(context: Context, type: DialogType, msg: String) {

            val builder = AlertDialog.Builder(context)
            val title = type.toString()
            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setPositiveButton("OK", { _, _ -> })
            builder.show()
        }
    }

}