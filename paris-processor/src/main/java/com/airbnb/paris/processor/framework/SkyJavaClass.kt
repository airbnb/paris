package com.airbnb.paris.processor.framework

import com.squareup.javapoet.*

internal abstract class SkyJavaClass(
        protected var packageName: String = "",
        protected var name: String = "",
        private val block: TypeSpec.Builder.() -> Unit
) {

    fun build(): TypeSpec {
        val builder = TypeSpec.classBuilder(name)
        builder.block()
        return builder.build()
    }

    fun write() {
        JavaFile.builder(packageName, build()).build().writeTo(filer)
    }
}
