package com.airbnb.paris

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.util.AttributeSet

class Style private constructor(
        private val attributeMap: Map<Int, Int>?,
        val attributeSet: AttributeSet?,
        @StyleRes val styleRes: Int,
        val config: Config?) {

    private constructor(builder: Builder) : this(builder.attributeMap, null, 0, null)
    @JvmOverloads constructor(attributeSet: AttributeSet, config: Config? = null) : this(null, attributeSet, 0, config)
    @JvmOverloads constructor(@StyleRes styleRes: Int, config: Config? = null) : this(null, null, styleRes, config)

    /**
     * Config objects are automatically passed from [Style] to [Style]. They
     * are simply a collection of objects with some helpers to sort through them. It is up to each
     * style to declare, retrieve, and act upon the configuration option that they are interested
     * in.
     *
     * Option objects can be as simple as enum values, or as complex as fully fledged objects, such
     * as listeners.
     */
    data class Config(val options: Set<Option>) {

        interface Option {
            override fun hashCode(): Int
            override fun equals(other: Any?): Boolean
        }

        class Builder internal constructor() {

            private var options: MutableSet<Option> = mutableSetOf()

            internal constructor(config: Config) : this() {
                options = config.options.toMutableSet()
            }

            fun addOption(option: Option): Builder {
                options.add(option)
                return this
            }

            fun build(): Config {
                return Config(options)
            }
        }

        companion object {
            fun builder(): Builder {
                return Builder()
            }
        }

        fun toBuilder(): Builder {
            return Builder(this)
        }

        fun contains(option: Option): Boolean {
            return options.contains(option)
        }

        fun <T : Option> get(optionClass: Class<out T>): T? {
            return options
                    .firstOrNull { optionClass.isInstance(it) }
                    ?.let { optionClass.cast(it) }
        }
    }

    class Builder internal constructor() {

        internal val attributeMap = HashMap<Int, Int>()

        fun put(@AttrRes attrRes: Int, valueRes: Int): Builder {
            attributeMap.put(attrRes, valueRes)
            return this
        }

        fun build(): Style = Style(this)
    }

    /**
     * Visible for debug
     */
    interface DebugListener {
        fun beforeTypedArrayProcessed(style: Style, typedArray: TypedArrayWrapper)
    }

    companion object {
        internal val EMPTY = Style(null, null, 0, null)

        fun builder(): Builder = Builder()
    }

    /**
     * Visible for debug
     */
    var debugListener: DebugListener? = null

    @SuppressLint("Recycle")
    fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper = when {
        attributeMap != null -> MapTypedArrayWrapper(context.resources, attrs, attributeMap)
        attributeSet != null -> TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(attributeSet, attrs, 0, styleRes))
        styleRes != 0 -> TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(styleRes, attrs))
        else -> EmptyTypedArrayWrapper
    }

    fun hasOption(option: Config.Option): Boolean {
        return config != null && config.contains(option)
    }
}