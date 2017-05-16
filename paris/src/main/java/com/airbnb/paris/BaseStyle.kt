package com.airbnb.paris

import android.content.res.TypedArray
import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.view.View

/**
 * A helper class for styles to:
 *
 *  * Explicitely declare parents to be applied automatically
 *  * Read attribute values from either a style resource or an [android.util.AttributeSet]
 *
 */
abstract class BaseStyle<T : View> : Style<T> {

    abstract fun attrSet(): AttributeSet?

    @StyleRes
    abstract fun styleRes(): Int

    abstract fun config(): Style.Config?

    /**
     * Instantiates and applies the style classes returned here right before this style is applied
     * itself.

     * Note: when using [Styles.change] to apply styles the entire hierarchy of View
     * styles is automatically applied. For example changing a [android.widget.TextView]
     * will automatically apply both [TextViewStyle] and [ViewStyle], because
     * [View] is a parent of [android.widget.TextView]. As a result it is recommended
     * not to return style classes here that will automatically be detected following the
     * [View] hierarchy.

     * @return  Parent styles to be applied before this one is. Each style is considered in-order,
     * *          with the first taking precedence over the following ones.
     */
    protected open fun parents(): List<Class<out Style<in T>>> {
        return emptyList<Class<out Style<in T>>>()
    }

    protected open fun attributes(): IntArray? {
        return null
    }

    /**
     * Called before [.processAttribute] would be called, regardless of whether it is actually called or not.
     */
    protected open fun beforeProcessAttributes(view: T) {
        // Default implementation does nothing
    }

    /**
     * Iterates over the [AttributeSet] and/or [StyleRes]'s [TypedArray] and
     * calls [.processAttribute] for each attribute.
     */
    protected open fun processAttributes(view: T) {
        val context = view.context

        val hasStyleData = attrSet() != null || styleRes() != 0
        val hasUnstyledAttributes = attributes() != null && attributes()!!.size > 0
        if (hasStyleData && hasUnstyledAttributes) {
            val typedArray = StyleUtils.obtainStyledAttributes(context, attrSet(), styleRes(), attributes())
            if (typedArray != null) {
                var i = 0
                val N = typedArray!!.getIndexCount()
                while (i < N) {
                    processAttribute(view, typedArray, typedArray!!.getIndex(i))
                    i++
                }
                typedArray!!.recycle()
            }
        }
    }

    protected open fun processAttribute(view: T, typedArray: TypedArray, index: Int) {
        // Default implementation does nothing
    }

    /**
     * Called after [.processAttribute] would be called, regardless of whether it is actually called or not.
     */
    protected open fun afterProcessAttributes(view: T) {
        // Default implementation does nothing
    }

    override fun applyTo(view: T) {
        // This applies the explicitly declared parents
        for (parentClass in parents()) {
            StyleUtils.create(parentClass, attrSet(), styleRes(), config()).applyTo(view)
        }

        beforeProcessAttributes(view)
        processAttributes(view)
        afterProcessAttributes(view)
    }
}
