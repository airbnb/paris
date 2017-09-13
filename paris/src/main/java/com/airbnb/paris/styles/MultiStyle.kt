package com.airbnb.paris.styles

import android.annotation.*
import android.content.*
import com.airbnb.paris.*
import com.airbnb.paris.typed_array_wrappers.*

data class MultiStyle internal constructor(private val name: String, private val styles: List<Style>) : Style {

    constructor(name: String, vararg styles: Style) : this(name, styles.toList())
    constructor(name: String, vararg styleRes: Int) : this(name, styleRes.map { ResourceStyle(it) })

    /**
     * Presumably multistyles would never be constructed with a SimpleStyle containing an
     * AttributeSet, which is the only reason why we wouldn't want to apply the parent style applier
     */
    override val shouldApplyParent = true

    override fun name(context: Context): String = name

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper {
        val wrappers = styles.map { it.obtainStyledAttributes(context, attrs) }
        return MultiTypedArrayWrapper(wrappers)
    }
}
