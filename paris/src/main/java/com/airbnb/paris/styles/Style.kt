package com.airbnb.paris.styles

import android.content.*
import com.airbnb.paris.typed_array_wrappers.*

interface Style {

    // TODO Better name
    val shouldApplyParent: Boolean

    fun name(context: Context): String

    fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper
}
