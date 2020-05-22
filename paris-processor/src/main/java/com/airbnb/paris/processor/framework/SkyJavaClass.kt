package com.airbnb.paris.processor.framework

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element

internal abstract class SkyJavaClass(override val processor: SkyProcessor) : WithSkyProcessor {

    protected abstract val packageName: String
    protected abstract val name: String
    protected abstract val block: TypeSpec.Builder.() -> Unit
    protected abstract val originatingElements: List<Element>

    fun build(): TypeSpec {
        val builder = TypeSpec.classBuilder(name)
        originatingElements.forEach {
            builder.addOriginatingElement(it)
        }
        builder.block()
        return builder.build()
    }

    fun write() {
        JavaFile.builder(packageName, build()).build().writeTo(filer)
    }
}
