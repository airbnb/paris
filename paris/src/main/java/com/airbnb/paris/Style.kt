package com.airbnb.paris

import android.content.Context
import android.view.View

interface Style {

    /**
     * Visible for debug
     */
    interface DebugListener {
        // TODO Rename
        fun beforeTypedArrayProcessed(view: View, style: Style, attributes: IntArray, attributesWithDefaultValue: IntArray?, typedArray: TypedArrayWrapper)
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
