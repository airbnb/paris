package com.airbnb.paris.processor.framework

import com.squareup.javapoet.*

abstract class SkyJavaClass<out T : SkyProcessor>(
        processor: T,
        protected var packageName: String = "",
        protected var name: String = "",
        val block: TypeSpec.Builder.() -> Unit
) : SkyHelper<T>(processor) {

    fun build(): TypeSpec {
        val builder = TypeSpec.classBuilder(name)
        builder.block()
        return builder.build()
    }

    fun write() {
        JavaFile.builder(packageName, build()).build().writeTo(filer)
    }
}
