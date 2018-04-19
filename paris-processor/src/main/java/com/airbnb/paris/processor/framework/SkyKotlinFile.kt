package com.airbnb.paris.processor.framework

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.ProcessingEnvironment


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
     * See [kaptOutputPath]
     */
    fun write(outputPath: String) {
        build().writeTo(File(outputPath))
    }
}

// This option will be present when processed by kapt, and it tells us where to put our
// generated kotlin files
// https://github.com/JetBrains/kotlin-examples/blob/master/gradle/kotlin-code-generation
// /annotation-processor/src/main/java/TestAnnotationProcessor.kt
private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

/**
 * Get the directory name where kapt output files should be placed.
 *
 * If null, this is not being processed by kapt, so we can't generate kotlin code.
 */
val ProcessingEnvironment.kaptOutputPath: String?
    get() {
        // Need to change the path because of https://youtrack.jetbrains.com/issue/KT-19097
        return options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            ?.replace("kaptKotlin", "kapt")
    }
