package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XFiler
import com.airbnb.paris.processor.BaseProcessor
import com.squareup.kotlinpoet.FileSpec


internal abstract class SkyKotlinFile( val processor: BaseProcessor) {

    protected abstract val packageName: String
    protected abstract val name: String
    protected abstract val block: FileSpec.Builder.() -> Unit

    fun build(): FileSpec {
        return FileSpec.builder(packageName, name).run {
            block()
            build()
        }
    }

    /**
     * If this module is being processed with kapt then the file is written, otherwise this is a no-op.
     */
    fun write(mode: XFiler.Mode = XFiler.Mode.Aggregating) {
        processor.filer.write(build(), mode)
    }
}
