package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.addOriginatingElement
import com.airbnb.paris.processor.BaseProcessor
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

internal abstract class SkyJavaClass(val processor: BaseProcessor) {

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

    fun write(mode: XFiler.Mode = XFiler.Mode.Aggregating) {
        val javaFile = JavaFile.builder(packageName, build()).build()
        processor.filer.write(javaFile, mode)
    }
}
