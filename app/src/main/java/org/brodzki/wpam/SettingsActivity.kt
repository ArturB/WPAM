package org.brodzki.wpam

import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*
import org.brodzki.wpam.dao.User

class SettingsActivity : SignedInActivity() {

    /**
     * LIFECYCLE HANDLERS
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        loggedUser = intent.extras.getSerializable(LOGGED_USER) as User

        //load saved settings
        datasource_input.setText(
                getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(DEFAULT_DATA_SOURCE, "")
        )
        low_proximity_input.setText(
                getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getFloat(DEFAULT_LOW_PROXIMITY, DEFAULT_LOW_PROXIMITY_VALUE).toString()
        )
        high_proximity_input.setText(
                getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getFloat(DEFAULT_HIGH_PROXIMITY, DEFAULT_HIGH_PROXIMITY_VALUE).toString()
        )

        //set buttons handlers
        apply_global_settings_button.setOnClickListener {
            applyGlobalSettings()
        }

    }

    /**
     * BUTTONS HANDLERS
     */

    private fun applyGlobalSettings() {

        //check if inputs are valid
        try {
            val lowProximity = low_proximity_input.text.toString().toFloat()
            val highProximity = high_proximity_input.text.toString().toFloat()

            if(!(isValidPercent(lowProximity) && isValidPercent(highProximity))) {
                settings_logs_textview.setTextColor(getColor(R.color.colorError))
                settings_logs_textview.text = getString(R.string.invalid_percent)
            }
            else {
                val sharedEditor = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit()
                sharedEditor.putString(DEFAULT_DATA_SOURCE, datasource_input.text.toString())
                sharedEditor.putFloat(DEFAULT_LOW_PROXIMITY, lowProximity)
                sharedEditor.putFloat(DEFAULT_HIGH_PROXIMITY, highProximity)
                sharedEditor.apply()

                settings_logs_textview.setTextColor(getColor(R.color.colorSuccess))
                settings_logs_textview.text = getString(R.string.settings_applied)
            }

        } catch(n: NumberFormatException) {
            settings_logs_textview.setTextColor(getColor(R.color.colorError))
            settings_logs_textview.text = getString(R.string.float_invalid)
        }
    }

    private fun isValidPercent(f: Float):Boolean {
        return f in 0.0f..100.0f
    }

}
