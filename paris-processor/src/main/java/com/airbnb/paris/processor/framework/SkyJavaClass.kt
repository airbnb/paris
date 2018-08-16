package com.airbnb.paris.processor.framework

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

internal abstract class SkyJavaClass(override val processor: SkyProcessor) : WithSkyProcessor {

    protected abstract val packageName: String
    protected abstract val name: String
    protected abstract val block: TypeSpec.Builder.() -> Unit

    fun build(): TypeSpec {
        val builder = TypeSpec.classBuilder(name)
        builder.block()
        return builder.build()
    }

    fun write() {
        JavaFile.builder(packageName, build()).build().writeTo(filer)
    }
}
