@file:Suppress("UNCHECKED_CAST")

package com.airbnb.paris

import android.support.annotation.*
import android.util.*
import android.view.*
import com.airbnb.paris.proxies.*
import com.airbnb.paris.styles.*
import com.airbnb.paris.typed_array_wrappers.*

@UiThread
abstract class StyleApplier<P, V : View> private constructor(val proxy: P, val view: V) {

    /**
     * Visible for debug
     */
    interface DebugListener {
        fun processAttributes(view: View, style: Style, attributes: IntArray, attributesWithDefaultValue: IntArray?, typedArray: TypedArrayWrapper)
    }

    protected constructor(proxy: Proxy<P, V>) : this(proxy.proxy, proxy.view)
    constructor(view: V) : this(view as P, view)

    /**
     * Visible for debug
     */
    var debugListener: StyleApplier.DebugListener? = null

    /**
     * Passing a null [AttributeSet] is a no-op, for convenience
     */
    fun apply(attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            apply(AttributeSetStyle(attributeSet))
        }
    }

    fun apply(@StyleRes styleRes: Int) {
        apply(ResourceStyle(styleRes))
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
            if (debugListener != null) {
                debugListener!!.processAttributes(view, style, attributes, attributesWithDefaultValue(), typedArray)
            } else {
                processAttributes(style, typedArray)
            }

            typedArray.recycle()
        }
    }

    protected open fun attributes(): IntArray? = null

    protected open fun attributesWithDefaultValue(): IntArray? = null

    protected open fun applyParent(style: Style) {}

    // TODO Rename to processStyleableChilds
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
