package com.airbnb.paris.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.content.res.AppCompatResources

import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper

// TODO Should this be used somewhere?
internal object StyleUtils {

    /**
     * Supports inflation of <vector> and <animated-vector> resources on devices where platform
     * support is not available
     */
    fun getDrawable(context: Context, array: TypedArrayWrapper, index: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            array.getDrawable(index)
        } else {
            val resourceId = array.getResourceId(index)
            if (resourceId != 0) {
                AppCompatResources.getDrawable(context, resourceId)
            } else {
                null
            }
        }
    }
}
