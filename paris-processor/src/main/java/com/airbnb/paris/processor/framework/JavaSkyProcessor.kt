package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.abstractions.XProcessingEnv
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

abstract class JavaSkyProcessor : AbstractProcessor(), WithJavaSkyProcessor {


    override val filer: Filer by lazy { processingEnv.filer }
    override val messager: Messager by lazy { processingEnv.messager }
    override val elements: Elements by lazy { processingEnv.elementUtils }
    override val types: Types by lazy { processingEnv.typeUtils }
    override val processingEnv: XProcessingEnv by lazy {
        XProcessingEnv.create(processingEnv)
    }

    override val memoizer: JavaSkyMemoizer by lazy { JavaSkyMemoizer(this) }
    override val loggedMessages = mutableListOf<Message>()

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            processRound(annotations, roundEnv)
        } catch (e: Throwable) {
            val rootCause = generateSequence(e) { it.cause }.last()
            messager.printMessage(Diagnostic.Kind.ERROR, rootCause.stackTraceToString())
        }

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

//class KspSkyProcessor : SymbolProcessor, WithKspSkyProcessor {
//    override lateinit var options: Map<String, String>
//    override lateinit var kotlinVersion: KotlinVersion
//    override lateinit var codeGenerator: CodeGenerator
//    override lateinit var logger: KSPLogger
//    override val loggedMessages: MutableList<Message> = mutableListOf()
//
//    override fun init(options: Map<String, String>, kotlinVersion: KotlinVersion, codeGenerator: CodeGenerator, logger: KSPLogger) {
//        this.options = options
//        this.kotlinVersion = kotlinVersion
//        this.codeGenerator = codeGenerator
//        this.logger = logger
//    }
//
//    override fun process(resolver: Resolver): List<KSAnnotated> {
//
//        return emptyList()
//    }
//
//    override fun finish() {
//
//    }
//}