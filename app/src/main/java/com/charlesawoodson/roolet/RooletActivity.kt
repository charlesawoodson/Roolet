package com.charlesawoodson.roolet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.airbnb.mvrx.MvRx
import com.charlesawoodson.roolet.contacts.ContactsFragment
import com.charlesawoodson.roolet.contacts.EditGroupArgs
import com.charlesawoodson.roolet.db.Group
import com.charlesawoodson.roolet.extensions.changeToolbarFont
import com.charlesawoodson.roolet.groupdetail.GroupDetailArgs
import com.charlesawoodson.roolet.groupdetail.GroupsDetailFragment
import com.charlesawoodson.roolet.groups.GroupsFragment
import com.charlesawoodson.roolet.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_container.*

class RooletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setSupportActionBar(toolbar)
        toolbar.changeToolbarFont()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<GroupsFragment>(R.id.container)
            }
        }
    }

    fun commitSettingsFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            replace<SettingsFragment>(
                R.id.container
            )
        }
    }

    fun commitContactsFragment(editGroupArgs: EditGroupArgs = EditGroupArgs()) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            replace<ContactsFragment>(
                R.id.container,
                null,
                Bundle().apply {
                    putParcelable(MvRx.KEY_ARG, editGroupArgs)
                }
            )
        }
    }

    fun commitGroupDetailFragment(group: Group) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            replace<GroupsDetailFragment>(
                R.id.container,
                null,
                Bundle().apply {
                    putParcelable(MvRx.KEY_ARG, GroupDetailArgs(group.groupId, group.title))
                }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}