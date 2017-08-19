@file:Suppress("UNCHECKED_CAST")

package com.airbnb.paris

import android.support.annotation.StyleRes
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.View

@UiThread
abstract class StyleApplier<out S : StyleApplier<S, T>, out T : View>(val view: T) {

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
            apply(Style(attributeSet, config))
        } else {
            apply(Style.EMPTY)
        }
        return this as S
    }

    fun apply(@StyleRes styleRes: Int): S {
        return apply(Style(styleRes, config))
    }

    open fun apply(style: Style): S {
        onStyleApply?.invoke(style)

        // Assumes that if the Style has an AttributeSet it's being applied during the View
        // initialization, in which case parents should be making the call themselves
        if (style.attributeSet == null) {
            applyParent(style)
        }

        applyDependencies(style)

        val attributes = attributes()
        if (attributes != null) {
            val typedArray = style.obtainStyledAttributes(view.context, attributes)

            // For debug purposes
            style.debugListener?.beforeTypedArrayProcessed(style, typedArray)

            processAttributes(style, typedArray)
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
}