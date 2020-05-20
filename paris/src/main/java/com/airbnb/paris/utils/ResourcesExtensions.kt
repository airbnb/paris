package com.airbnb.paris.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes
import com.airbnb.paris.styles.ResourceStyle

fun Resources.dpToPx(dps: Int) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps.toFloat(), displayMetrics).toInt()

fun Resources.getFloat(@AnyRes res: Int): Float {
    val outValue = TypedValue()
    getValue(res, outValue, true)
    return outValue.float
}

fun Resources.getLayoutDimension(@DimenRes res: Int): Int {
    val outValue = TypedValue()
    getValue(res, outValue, true)
    if (outValue.type >= TypedValue.TYPE_FIRST_INT
        && outValue.type <= TypedValue.TYPE_LAST_INT
    ) {
        return outValue.data
    } else {
        return outValue.getDimension(displayMetrics).toInt()
    }
}

fun Resources.getStyle(@StyleRes res: Int) = ResourceStyle(res)
