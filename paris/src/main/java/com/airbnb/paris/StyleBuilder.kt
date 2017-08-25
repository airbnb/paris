package com.airbnb.paris

import android.support.annotation.StyleRes
import android.util.AttributeSet

@Suppress("UNCHECKED_CAST")
abstract class StyleBuilder<out B : StyleBuilder<B, A>, out A : StyleApplier<*, *>> @JvmOverloads constructor(
        private val applier: A? = null,
        private val name: String = "a_programmatic_StyleBuilder") {

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
            else -> MultiStyle(name, styles)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StyleBuilder<*, *>

        if (name != other.name) return false
        if (applier != other.applier) return false
        if (builder != other.builder) return false
        if (styles != other.styles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (applier?.hashCode() ?: 0)
        result = 31 * result + builder.hashCode()
        result = 31 * result + styles.hashCode()
        return result
    }
}
