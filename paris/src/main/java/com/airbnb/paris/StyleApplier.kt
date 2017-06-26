@file:Suppress("UNCHECKED_CAST")

package com.airbnb.paris

import android.support.annotation.StyleRes
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.View

@UiThread
abstract class StyleApplier<out S : StyleApplier<S, T>, out T : View>(val view: T) {

    private var config: Style.Config = Style.Config.builder().build()

    fun addOption(option: Style.Config.Option): S {
        config = config.toBuilder().addOption(option).build()
        return this as S
    }

    fun apply(attributeSet: AttributeSet?): S {
        // TODO  Apply even if attributeSet is null so that default values work even when creating a view with no AttributeSet
        // We allow null AttributeSets purely for convenience here
        if (attributeSet != null) {
            apply(Style(attributeSet, config))
        }
        return this as S
    }

    fun apply(@StyleRes styleRes: Int): S {
        return apply(Style(styleRes, config))
    }

    open fun apply(style: Style): S {
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

            if (typedArray != null) {
                processAttributes(style, typedArray)
                typedArray.recycle()
            }
        }

        return this as S
    }

    protected open fun attributes(): IntArray? {
        return null
    }

    protected open fun applyParent(style: Style) {}

    protected open fun applyDependencies(style: Style) {}

    protected open fun processAttributes(style: Style, a: TypedArrayWrapper) {}
}