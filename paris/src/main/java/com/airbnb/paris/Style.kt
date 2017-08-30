package com.airbnb.paris

import android.content.Context
import android.view.View

interface Style {

    /**
     * Visible for debug
     */
    interface DebugListener {
        fun processAttributes(view: View, style: Style, attributes: IntArray, attributesWithDefaultValue: IntArray?, typedArray: TypedArrayWrapper)
    }

    /**
     * Visible for debug
     */
    var debugListener: DebugListener?

    // TODO Better name
    val shouldApplyParent: Boolean

    fun name(context: Context): String

    fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper
}
