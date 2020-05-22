package com.airbnb.paris.styles

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.IntRange
import androidx.annotation.StyleRes
import com.airbnb.paris.typed_array_wrappers.TypedArrayTypedArrayWrapper
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper

data class ResourceStyle constructor(
    @StyleRes @IntRange(from = 0) private val styleRes: Int,
    private var name: String? = null
) : Style {

    override val shouldApplyParent = true

    override val shouldApplyDefaults = true

    override fun name(context: Context): String =
        context.resources.getResourceEntryName(styleRes)

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper =
        TypedArrayTypedArrayWrapper(context, context.obtainStyledAttributes(styleRes, attrs))
}
