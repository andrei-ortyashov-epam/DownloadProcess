package com.example.downloadprocess.providers

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

class ResourceProvider(
    private val context: Context
) {

    fun getQuantityString(@PluralsRes stringRes: Int, value: Int): String =
        context.resources.getQuantityString(stringRes, value, value)

    fun getString(@StringRes stringRes: Int): String =
        context.getString(stringRes)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String =
        context.getString(resId, *formatArgs)

    fun getText(@StringRes stringRes: Int): CharSequence =
        context.getText(stringRes)
}
