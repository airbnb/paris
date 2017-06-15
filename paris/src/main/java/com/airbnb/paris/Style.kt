package com.airbnb.paris

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.util.SparseIntArray

class Style private constructor(
        val attributeMap: SparseIntArray?,
        val attributeSet: AttributeSet?,
        @StyleRes val styleRes: Int,
        val config: Config?) {

    private constructor(builder: Builder) : this(builder.attributeMap, null, 0, null)

    constructor(attributeSet: AttributeSet) : this(null, attributeSet, 0, null)
    constructor(attributeSet: AttributeSet, config: Config) : this(null, attributeSet, 0, config)

    constructor(@StyleRes styleRes: Int) : this(null, null, styleRes, null)
    constructor(@StyleRes styleRes: Int, config: Config) : this(null, null, styleRes, config)

    /**
     * Config objects are automatically passed from [BaseStyle] to [BaseStyle]. They
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

    // TODO
    class Builder internal constructor() {

        internal val attributeMap = SparseIntArray()

        fun put(@AttrRes attrRes: Int, valueRes: Int): Builder {
            attributeMap.put(attrRes, valueRes)
            return this
        }

        fun build(): Style {
            return Style(this)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    internal interface TestingListener {
        fun beforeTypedArrayProcessed(typedArray: TypedArrayWrapper?)
    }

    internal var testingListener: TestingListener? = null

    @SuppressLint("Recycle")
    fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper? {
        if (attributeMap != null) {
            val filteredAttributeMap = SparseIntArray()
            for (attrRes in attrs) {
                val value = attributeMap.get(attrRes, -1)
                if (value != -1) {
                    filteredAttributeMap.put(attrRes, value)
                }
            }
            return SparseIntArrayTypedArrayWrapper(context.resources, filteredAttributeMap)
        } else if (attributeSet != null) {
            return TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(attributeSet, attrs, 0, styleRes))
        } else if (styleRes != 0) {
            return TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(styleRes, attrs))
        } else {
            return null
        }
    }

    fun hasOption(option: Config.Option): Boolean {
        return config != null && config.contains(option)
    }
}