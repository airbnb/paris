package com.airbnb.paris

import android.content.Context

interface Style {

    /**
     * Visible for debug
     */
    interface DebugListener {
        // TODO Rename
        fun beforeTypedArrayProcessed(style: Style, typedArray: TypedArrayWrapper)
    }

    // TODO Better name
    val shouldApplyParent: Boolean

    /**
     * Visible for debug
     */
    var debugListener: DebugListener?

    fun name(context: Context): String

    fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper
}
