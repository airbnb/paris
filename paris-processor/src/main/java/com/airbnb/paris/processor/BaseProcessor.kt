package com.airbnb.paris.processor

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XMessager
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Creates a unified abstraction for processors of both KSP and java annotation processing.
 */
abstract class BaseProcessor(var kspEnvironment: SymbolProcessorEnvironment? = null) : AbstractProcessor(), SymbolProcessor {

    lateinit var environment: XProcessingEnv
        private set

    val messager: XMessager
        get() = environment.messager

    val filer: XFiler
        get() = environment.filer

    val isKsp: Boolean get() = kspEnvironment != null

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.RELEASE_8

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        environment = XProcessingEnv.create(processingEnv)
    }

    final override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {

        // The expectation is that all files will be generated during the first round where we get
        // all the annotated elements. Then a second empty round will happen to give us a chance to
        // do more but we ignore it
        if (annotations.isNotEmpty()) {
            try {
                process(environment, XRoundEnv.create(environment, roundEnv))
            } catch (e: Throwable) {
                val rootCause = generateSequence(e) { it.cause }.last()
                messager.printMessage(Diagnostic.Kind.ERROR, rootCause.stackTraceToString())
            }
        }

        if (roundEnv.errorRaised()) {
            onError()
        }


        if (roundEnv.processingOver()) {
            finish()
        }

        return false
    }

    final override fun process(resolver: Resolver): List<KSAnnotated> {
        val kspEnvironment = requireNotNull(kspEnvironment)
        environment = XProcessingEnv.create(kspEnvironment, resolver)
        process(environment, XRoundEnv.create(environment))
        return emptyList()
    }

    abstract fun process(
        environment: XProcessingEnv,
        round: XRoundEnv
    )
}