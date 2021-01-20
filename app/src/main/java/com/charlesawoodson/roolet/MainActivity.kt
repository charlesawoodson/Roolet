package com.charlesawoodson.roolet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.charlesawoodson.roolet.contacts.CreateGroupActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.addGroupImageView).setOnClickListener {
            Intent(this, CreateGroupActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}