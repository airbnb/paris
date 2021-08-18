package com.airbnb.paris.processor.framework

import com.squareup.kotlinpoet.FileSpec


internal abstract class SkyKotlinFile(override val processor: JavaSkyProcessor) : WithJavaSkyProcessor {

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
    fun write() {
        build().writeTo(processor.filer)
    }
}
