package com.airbnb.paris

import android.content.Context

interface Style {

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

    /**
     * Visible for debug
     */
    interface DebugListener {
        // TODO Rename
        fun beforeTypedArrayProcessed(style: Style, typedArray: TypedArrayWrapper)
    }

    // TODO Better name
    val shouldApplyParent: Boolean

    /**
     * Visible for debug
     */
    var debugListener: DebugListener?

    fun name(context: Context): String

    fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper

    fun hasOption(option: Config.Option): Boolean

}
