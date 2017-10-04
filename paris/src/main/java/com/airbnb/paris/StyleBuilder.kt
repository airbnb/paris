package com.airbnb.paris

import android.support.annotation.*
import android.util.*
import com.airbnb.paris.styles.*

@Suppress("UNCHECKED_CAST")
abstract class StyleBuilder<out B : StyleBuilder<B, A>, out A : StyleApplier<*, *>> @JvmOverloads constructor(
        private val applier: A? = null,
        private var name: String = "a_programmatic_StyleBuilder") {

    protected var builder = ProgrammaticStyle.builder()

    private var styles = ArrayList<Style>()

    /**
     * Assigns a name to the style which will be displayed in "same attributes" assertions, ie this
     * is only useful for debugging
     */
    fun debugName(name: String): B {
        this.name = name
        return this as B
    }

    /**
     * Passing a null [AttributeSet] is a no-op, for convenience
     */
    fun add(attributeSet: AttributeSet?): B {
        if (attributeSet != null) {
            add(AttributeSetStyle(attributeSet))
        }
        return this as B
    }

    fun add(@StyleRes styleRes: Int): B = add(ResourceStyle(styleRes))

    fun add(style: Style): B {
        consumeProgrammaticStyleBuilder()
        styles.add(style)
        return this as B
    }

    fun build(): Style {
        // If no other styles were added then the style from the builder will be returned, this
        // ensures the name from this builder is correctly set on the returned style
        if (styles.size == 0) {
            builder.debugName(name)
        }

        consumeProgrammaticStyleBuilder()
        return when (styles.size) {
            0 -> EmptyStyle
            1 -> styles.first()
            else -> MultiStyle(name, styles)
        }
    }

    fun apply(): A {
        applier!!.apply(build())
        return applier
    }

    protected fun consumeProgrammaticStyleBuilder() {
        if (!builder.isEmpty()) {
            styles.add(builder.build())
            builder = ProgrammaticStyle.builder()
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
