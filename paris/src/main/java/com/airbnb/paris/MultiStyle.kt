package com.airbnb.paris

import android.annotation.SuppressLint
import android.content.Context

data class MultiStyle internal constructor(private val styles: List<Style>) : Style {

    constructor(vararg styles: Style) : this(styles.toList())
    constructor(vararg styleRes: Int) : this(styleRes.map { SimpleStyle(it) })

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
    override fun name(context: Context): String = "multistyle"

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper {
        val wrappers = styles.map { it.obtainStyledAttributes(context, attrs) }
        return MultiTypedArrayWrapper(wrappers)
    }
}
