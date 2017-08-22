package com.airbnb.paris

import android.support.annotation.StyleRes
import android.util.AttributeSet

@Suppress("UNCHECKED_CAST")
abstract class StyleBuilder<out B : StyleBuilder<B, A>, out A : StyleApplier<*, *>>(private val applier: A? = null) {

    protected var builder = SimpleStyle.builder()

    private var styles = ArrayList<Style>()

    fun add(attributeSet: AttributeSet?): B {
        if (attributeSet != null) {
            add(SimpleStyle(attributeSet))
        } else {
            add(SimpleStyle.EMPTY)
        }
        return this as B
    }

    fun add(@StyleRes styleRes: Int): B = add(SimpleStyle(styleRes))

    fun add(style: Style): B {
        consumeStyleBuilder()
        styles.add(style)
        return this as B
    }

    fun build(): Style {
        consumeStyleBuilder()
        return when (styles.size) {
            0 -> SimpleStyle.EMPTY
            1 -> styles.first()
            else -> MultiStyle(styles)
        }
    }

    fun apply(): A {
        applier!!.apply(build())
        return applier
    }

    private fun consumeStyleBuilder() {
        if (!builder.isEmpty()) {
            styles.add(builder.build())
            builder = SimpleStyle.builder()
        }
    }
}
