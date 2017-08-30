package com.airbnb.paris.styles

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.IntRange
import android.support.annotation.StyleRes
import com.airbnb.paris.Style
import com.airbnb.paris.TypedArrayTypedArrayWrapper
import com.airbnb.paris.TypedArrayWrapper

data class ResourceStyle constructor(
        @StyleRes @IntRange(from = 0) private val styleRes: Int,
        private var name: String? = null) : Style {

    override val shouldApplyParent = true

    /**
     * Visible for debug
     */
    override var debugListener: Style.DebugListener? = null

    override fun name(context: Context): String =
        context.resources.getResourceEntryName(styleRes)

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper =
            TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(styleRes, attrs))
}
