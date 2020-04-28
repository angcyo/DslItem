package com.angcyo.item.base

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * 免入侵, 获取[Content]对象
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class LibInitProvider : ContentProvider() {

    companion object {
        lateinit var contentProvider: Context
    }

    override fun onCreate(): Boolean {
        contentProvider = context!!.applicationContext
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = -1

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = -1

    override fun getType(uri: Uri): String? = null
}