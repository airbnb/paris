package com.airbnb.paris.styles

import android.content.Context
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper

interface Style {

    // TODO Better name
    val shouldApplyParent: Boolean

    /**
     * If true then default attribute values will be applied for missing attributes, if false they
     * won't
     */
    val shouldApplyDefaults: Boolean

    fun name(context: Context): String

    fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper
}
