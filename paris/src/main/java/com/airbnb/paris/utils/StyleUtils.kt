package com.airbnb.paris.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.content.res.AppCompatResources

import com.airbnb.paris.TypedArrayWrapper

internal object StyleUtils {

    fun getDrawable(context: Context, a: TypedArrayWrapper, index: Int): Drawable? {
        return if (a.isNull(index)) null else getDrawableCompat(context, a, index)
    }

    /**
     * Use this to load a vector drawable from a TypedArray in a backwards compatible fashion
     */
    private fun getDrawableCompat(context: Context, array: TypedArrayWrapper, index: Int): Drawable? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return array.getDrawable(index)
        } else {
            val resourceId = array.getResourceId(index, -1)
            return if (resourceId != -1) {
                AppCompatResources.getDrawable(context, resourceId)
            } else {
                null
            }
        }
    }
}
