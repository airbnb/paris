@file:Suppress("UNCHECKED_CAST")

package com.airbnb.paris

import android.support.annotation.StyleRes
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.proxy.Proxy

@UiThread
abstract class StyleApplier<P, V : View> private constructor(val proxy: P, val view: V) {

    protected constructor(proxy: Proxy<P, V>) : this(proxy.proxy, proxy.view)
    constructor(view: V) : this(view as P, view)

    /**
     * Passing a null [AttributeSet] will apply default values, if any
     */
    fun apply(attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            apply(SimpleStyle(attributeSet))
        } else {
            apply(SimpleStyle.EMPTY)
        }
    }

    fun apply(@StyleRes styleRes: Int) {
        apply(SimpleStyle(styleRes))
    }

    open fun apply(style: Style) {
        // Assumes that if the Style has an AttributeSet it's being applied during the View
        // initialization, in which case parents should be making the call themselves
        if (style.shouldApplyParent) {
            applyParent(style)
        }

        val attributes = attributes()
        if (attributes != null) {
            val typedArray = style.obtainStyledAttributes(view.context, attributes)

            processStyleableFields(style, typedArray)

            // For debug purposes
            if (style.debugListener != null) {
                style.debugListener!!.beforeTypedArrayProcessed(view, style, attributes, attributesWithDefaultValue(), typedArray)
            } else {
                processAttributes(style, typedArray)
            }

            typedArray.recycle()
        }
    }

    protected open fun attributes(): IntArray? = null

    /**
     * Visible for debug
     */
    open fun attributesWithDefaultValue(): IntArray? = null

    protected open fun applyParent(style: Style) {}

    protected open fun processStyleableFields(style: Style, a: TypedArrayWrapper) {}

    protected open fun processAttributes(style: Style, a: TypedArrayWrapper) {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StyleApplier<*, *>

        if (proxy != other.proxy) return false
        if (view != other.view) return false

        return true
    }

    override fun hashCode(): Int {
        var result = proxy?.hashCode() ?: 0
        result = 31 * result + view.hashCode()
        return result
    }
}
