package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.addOriginatingElement
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

internal abstract class SkyJavaClass(override val processor: JavaSkyProcessor) : WithJavaSkyProcessor {

    protected abstract val packageName: String
    protected abstract val name: String
    protected abstract val block: TypeSpec.Builder.() -> Unit
    protected abstract val originatingElements: List<XElement>

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
