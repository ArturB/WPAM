package org.brodzki.wpam

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import org.brodzki.wpam.dao.User

open class SignedInActivity : VoteForUsActivity() {

    lateinit var loggedUser: User

    /**
     * HANDLE GLOBAL MENU
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        this.menuInflater.inflate(R.menu.logged_menu, menu)
        return true
    }

    /**
     * HANDLE LOGGED USER
     */
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        when(item!!.itemId) {

            R.id.action_back -> {

            }

            R.id.application_settings -> {
                val intent = Intent(this, SettingsActivity::class.java).apply {
                    putExtra(LOGGED_USER, loggedUser)
                }
                startActivity(intent)
            }

            R.id.action_settings -> {
                val intent = Intent(this, EditUserActivity::class.java).apply {
                    putExtra("logged_user", loggedUser)
                }
                startActivity(intent)
            }

            R.id.action_logout -> {
                this.finish()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            R.id.action_close -> {
                finish()
            }

        }
        return true
    }



}