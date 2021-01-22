package com.charlesawoodson.roolet.repository

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.database.Cursor


class QueryHandler(cr: ContentResolver?) : AsyncQueryHandler(cr) {

    override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
        super.onQueryComplete(token, cookie, cursor)
    }


}

