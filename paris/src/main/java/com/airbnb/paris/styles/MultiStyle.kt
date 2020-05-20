package com.airbnb.paris.styles

import android.annotation.SuppressLint
import android.content.Context
import com.airbnb.paris.typed_array_wrappers.MultiTypedArrayWrapper
import com.airbnb.paris.typed_array_wrappers.TypedArrayTypedArrayWrapper
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper

data class MultiStyle internal constructor(
    private val name: String,
    private val styles: List<Style>
) : Style {

    constructor(name: String, vararg styles: Style) : this(name, styles.toList())
    constructor(name: String, vararg styleRes: Int) : this(name, styleRes.map { ResourceStyle(it) })

    /**
     * Presumably multistyles would never be constructed with a SimpleStyle containing an
     * AttributeSet, which is the only reason why we wouldn't want to apply the parent style applier
     */
    override val shouldApplyParent = true

    override val shouldApplyDefaults = true

    override fun name(context: Context): String = name

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper {
        val themeTypedArrayWrapper = TypedArrayTypedArrayWrapper(context, context.obtainStyledAttributes(attrs))
        val styleWrappers = styles.map { it.obtainStyledAttributes(context, attrs) }
        // Returns theme attributes by default.
        return MultiTypedArrayWrapper(listOf(themeTypedArrayWrapper) + styleWrappers, attrs)
    }

    companion object {

        fun fromStyles(name: String, styles: List<Style>): Style {
            return when (styles.size) {
                0 -> EmptyStyle
                1 -> styles.first()
                else -> MultiStyle(name, styles)
            }
        }
    }
}
