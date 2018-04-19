package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.ParisProcessor
import com.squareup.kotlinpoet.FileSpec
import java.io.File


internal abstract class SkyKotlinFile(
    private val packageName: String,
    private val name: String,
    private val block: FileSpec.Builder.() -> Unit
) {

    fun build(): FileSpec {
        return FileSpec.builder(packageName, name).run {
            block()
            build()
        }
    }

    /**
     * If this module is being processed with kapt then the file is written, otherwise this is a no-op.
     */
    fun write() {
        kaptOutputPath?.let {
            build().writeTo(File(it))
        }
    }
}
