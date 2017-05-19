package com.airbnb.paris

import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.view.View

abstract class StyleApplier<out T : View>(protected val view: T) {

    protected abstract fun attributes(): IntArray

    private var config: Style.Config = Style.Config.builder().build()

    fun addOption(option: Style.Config.Option): StyleApplier<T> {
        config = config.toBuilder().addOption(option).build()
        return this
    }

    fun apply(attributeSet: AttributeSet?): StyleApplier<T> {
        // We allow null AttributeSets purely for convenience here
        if (attributeSet != null) {
            apply(Style(attributeSet, config))
        }
        return this
    }

    fun apply(@StyleRes styleRes: Int): StyleApplier<T> {
        return apply(Style(styleRes, config))
    }

    fun apply(style: Style): StyleApplier<T> {
        // Assumes that if the Style has an AttributeSet it's being applied during the View
        // initialization, in which case parents should be making the call themselves
        if (style.attributeSet == null) {
            applyParent(style)
        }

        beforeProcessAttributes(style)

        val typedArray = style.obtainStyledAttributes(view.context, attributes())
        if (typedArray != null) {
            for (i in 0..typedArray.getIndexCount()-1) {
                processAttribute(style, typedArray, typedArray.getIndex(i))
            }
        }

        afterProcessAttributes(style)

        return this
    }

    protected open fun applyParent(style: Style) {}

    protected open fun beforeProcessAttributes(style: Style) {}

    protected abstract fun processAttribute(style: Style, a: TypedArrayWrapper, index: Int)

    protected open fun afterProcessAttributes(style: Style) {}
}