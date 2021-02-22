package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.abstractions.XElement
import com.airbnb.paris.processor.abstractions.javac.JavacElement
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

internal abstract class SkyJavaClass(override val processor: JavaSkyProcessor) : WithJavaSkyProcessor {

    protected abstract val packageName: String
    protected abstract val name: String
    protected abstract val block: TypeSpec.Builder.() -> Unit
    protected abstract val originatingElements: List<XElement>

    fun build(): TypeSpec {
        val builder = TypeSpec.classBuilder(name)
        originatingElements.filterIsInstance<JavacElement>().forEach {
            builder.addOriginatingElement(it.element)
        }
        builder.block()
        return builder.build()
    }

    fun write() {
        JavaFile.builder(packageName, build()).build().writeTo(filer)
    }
}
