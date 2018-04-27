package com.airbnb.paris.processor.framework

import javax.annotation.processing.*
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

abstract class SkyProcessor : AbstractProcessor() {

    companion object {
        lateinit var INSTANCE: SkyProcessor
    }

    internal val filer: Filer by lazy { processingEnv.filer }
    internal val messager: Messager by lazy { processingEnv.messager }
    internal val elements: Elements by lazy { processingEnv.elementUtils }
    internal val types: Types by lazy { processingEnv.typeUtils }

    /**
     * The directory name where kapt output files should be placed.
     *
     * If null, this is not being processed by kapt, so we can't generate kotlin code.
     */
    internal val kaptOutputPath: String? by lazy {
        processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            // Need to change the path because of https://youtrack.jetbrains.com/issue/KT-19097
            ?.replace("kaptKotlin", "kapt")
    }

    internal val loggedMessages = mutableListOf<Message>()

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        INSTANCE = this
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        processRound(annotations, roundEnv)

        if (roundEnv.processingOver()) {
            processingOver()
        }

        return claimAnnotations(annotations, roundEnv)
    }

    abstract fun processRound(annotations: Set<TypeElement>, roundEnv: RoundEnvironment)

    abstract fun claimAnnotations(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean

    abstract fun processingOver()
}

/**
 * This option will be present when processed by kapt, and it tells us where to put our
 * generated kotlin files
 * https://github.com/JetBrains/kotlin-examples/blob/master/gradle/kotlin-code-generation/annotation-processor/src/main/java/TestAnnotationProcessor.kt
 */
private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
