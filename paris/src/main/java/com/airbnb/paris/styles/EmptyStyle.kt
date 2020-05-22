package com.airbnb.paris.styles

import android.annotation.SuppressLint
import android.content.Context
import com.airbnb.paris.typed_array_wrappers.EmptyTypedArrayWrapper
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper

object EmptyStyle : Style {

    // The parents might have default values that would get applied despite this style being empty
    override val shouldApplyParent = true

    override val shouldApplyDefaults = true

    override fun name(context: Context): String = "EmptyStyle"

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper =
        EmptyTypedArrayWrapper
}
