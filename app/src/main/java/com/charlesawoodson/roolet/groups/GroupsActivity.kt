package com.charlesawoodson.roolet.groups

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.airbnb.mvrx.MvRx
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.ContactsFragment
import com.charlesawoodson.roolet.contacts.EditGroupArgs
import com.charlesawoodson.roolet.extensions.changeToolbarFont
import com.charlesawoodson.roolet.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_container.*

class GroupsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        setSupportActionBar(toolbar)
        toolbar.changeToolbarFont()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<GroupsFragment>(R.id.container)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_group -> {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) -> {
                        commitContactsFragment()
                    }
                    else -> {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            PERMISSIONS_REQUEST_READ_CONTACTS
                        )
                    }
                }
                true
            }
            R.id.action_settings -> {
                Intent(applicationContext, SettingsActivity::class.java).apply {
                    startActivity(this)
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun commitContactsFragment() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            replace<ContactsFragment>(
                R.id.container,
                null,
                Bundle().apply {
                    putParcelable(MvRx.KEY_ARG, EditGroupArgs())
                }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val displayBackArrow = supportFragmentManager.backStackEntryCount > 1
        supportActionBar?.setDisplayHomeAsUpEnabled(displayBackArrow)
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val displayBackArrow = supportFragmentManager.backStackEntryCount > 1
        supportActionBar?.setDisplayHomeAsUpEnabled(displayBackArrow)
        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                commitContactsFragment()
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }
}