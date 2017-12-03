package com.airbnb.paris.styles

import android.annotation.*
import android.content.*
import android.support.annotation.*
import android.support.annotation.IntRange
import com.airbnb.paris.*
import com.airbnb.paris.typed_array_wrappers.*

data class ResourceStyle constructor(
        @StyleRes @IntRange(from = 0) val styleRes: Int,
        private var name: String? = null) : Style {

    override val shouldApplyParent = true

    override fun name(context: Context): String =
        context.resources.getResourceEntryName(styleRes)

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper =
            TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(styleRes, attrs))
}
