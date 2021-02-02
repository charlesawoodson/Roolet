package com.charlesawoodson.roolet.groupdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.MvRx
import com.charlesawoodson.roolet.R

class GroupDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {

            val groupId = intent.getLongExtra(MvRx.KEY_ARG, -1)

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<GroupsDetailFragment>(
                    R.id.container,
                    null,
                    Bundle().apply {
                        putLong(MvRx.KEY_ARG, groupId)
                    }
                )
            }
        }
    }
}