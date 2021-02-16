package com.charlesawoodson.roolet.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.MvRx
import com.charlesawoodson.roolet.R

class ContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        val groupData = intent.getParcelableExtra<EditGroupArgs>(MvRx.KEY_ARG)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ContactsFragment>(
                    R.id.container,
                    null,
                    Bundle().apply {
                        putParcelable(MvRx.KEY_ARG, groupData)
                    }
                )
            }
        }
    }
}