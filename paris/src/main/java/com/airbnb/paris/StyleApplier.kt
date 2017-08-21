@file:Suppress("UNCHECKED_CAST")

package com.airbnb.paris

import android.support.annotation.StyleRes
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.proxy.Proxy

@UiThread
abstract class StyleApplier<out S : StyleApplier<S, P, V>, P, V : View> private constructor(val proxy: P, val view: V) {

    protected constructor(proxy: Proxy<P, V>) : this(proxy.proxy, proxy.view)
    constructor(view: V) : this(view as P, view)

    /**
     * This is only public because the internal visibility Java interop is weird, do not use
     */
    // TODO  Fix visibility
    var onStyleApply: ((Style) -> Unit)? = null
        set(value) {
            if (field == null) {
                field = value
            } else {
                throw IllegalStateException("onStyleApply was already set")
            }
        }

    private var appliedStyles = ArrayList<Style>()

    private var config: Style.Config = Style.Config.builder().build()

    fun addOption(option: Style.Config.Option): S {
        config = config.toBuilder().addOption(option).build()
        return this as S
    }

    /**
     * Passing a null [AttributeSet] will apply default values, if any
     */
    fun apply(attributeSet: AttributeSet?): S {
        if (attributeSet != null) {
            apply(SimpleStyle(attributeSet, config))
        } else {
            apply(SimpleStyle.EMPTY)
        }
        return this as S
    }

    fun apply(@StyleRes styleRes: Int): S {
        return apply(SimpleStyle(styleRes, config))
    }

    open fun apply(style: Style): S {
        appliedStyles.add(style)

        onStyleApply?.invoke(style)

        // Assumes that if the Style has an AttributeSet it's being applied during the View
        // initialization, in which case parents should be making the call themselves
        if (style.shouldApplyParent) {
            applyParent(style)
        }

        applyDependencies(style)

        val attributes = attributes()
        if (attributes != null) {
            val typedArray = style.obtainStyledAttributes(view.context, attributes)

            // For debug purposes
            if (style.debugListener != null) {
                style.debugListener!!.beforeTypedArrayProcessed(style, typedArray)
            } else {
                processAttributes(style, typedArray)
            }

            typedArray.recycle()
        }

        return this as S
    }

    protected open fun attributes(): IntArray? {
        return null
    }

    /**
     * Visible for debug
     */
    open fun attributesWithDefaultValue(): IntArray? {
        return null
    }

    protected open fun applyParent(style: Style) {}

    protected open fun applyDependencies(style: Style) {}

    protected open fun processAttributes(style: Style, a: TypedArrayWrapper) {}

    /**
     * For debug purposes.
     *
     * Asserts that the attributes applied when using [styleRes] are the same as the aggregated
     * attributes already applied by this [StyleApplier]
     */
    fun assertAppliedSameAttributes(@StyleRes styleRes: Int) {
        assertAppliedSameAttributes(SimpleStyle(styleRes))
    }

    /**
     * For debug purposes.
     *
     * Asserts that the attributes applied when using [style] are the same as the aggregated
     * attributes already applied by this [StyleApplier]
     */
    fun assertAppliedSameAttributes(style: Style) {
        StyleApplierUtils.assertSameAttributes(this, style, MultiStyle(appliedStyles, null))
    }
}