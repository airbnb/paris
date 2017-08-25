package com.airbnb.paris

import android.annotation.SuppressLint
import android.content.Context

data class MultiStyle internal constructor(private val name: String, private val styles: List<Style>) : Style {

    constructor(name: String, vararg styles: Style) : this(name, styles.toList())
    constructor(name: String, vararg styleRes: Int) : this(name, styleRes.map { SimpleStyle(it) })

    /**
     * Presumably multistyles would never be constructed with a SimpleStyle containing an
     * AttributeSet, which is the only reason why we wouldn't want to apply the parent style applier
     */
    override val shouldApplyParent = true

    /**
     * Visible for debug
     */
    override var debugListener: Style.DebugListener? = null

    // TODO Construct name that makes sense
    override fun name(context: Context): String = name

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper {
        val wrappers = styles.map { it.obtainStyledAttributes(context, attrs) }
        return MultiTypedArrayWrapper(wrappers)
    }
}
