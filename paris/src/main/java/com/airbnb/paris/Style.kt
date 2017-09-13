package com.airbnb.paris

import android.content.*

interface Style {

    // TODO Better name
    val shouldApplyParent: Boolean

    fun name(context: Context): String

    fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper
}
