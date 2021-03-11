package com.charlesawoodson.roolet.groups

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.extensions.changeToolbarFont
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_group -> {
                Toast.makeText(applicationContext, "Add Group Action", Toast.LENGTH_LONG).show()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                true
            }
            R.id.action_settings -> {
                Toast.makeText(applicationContext, "Settings Action", Toast.LENGTH_LONG).show()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}