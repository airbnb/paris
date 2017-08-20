package com.airbnb.paris

@Suppress("UNCHECKED_CAST")
abstract class StyleBuilder<out B : StyleBuilder<B, A>, out A : StyleApplier<*, *, *>>(private val applier: A? = null) {

    protected val builder by lazy { Style.builder() }

    fun build(): Style = builder.build()

    fun apply(): A {
        applier!!.apply(build())
        return applier
    }
}
