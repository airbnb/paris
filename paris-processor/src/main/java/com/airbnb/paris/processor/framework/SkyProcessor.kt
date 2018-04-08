package com.airbnb.paris.processor.framework

import javax.annotation.processing.*
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

abstract class SkyProcessor : AbstractProcessor() {

    companion object {
        lateinit var INSTANCE: SkyProcessor
    }

    lateinit var filer: Filer
    lateinit var messager: Messager
    lateinit var elements: Elements
    lateinit var types: Types

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        filer = processingEnv.filer
        messager = processingEnv.messager
        elements = processingEnv.elementUtils
        types = processingEnv.typeUtils

        INSTANCE = this
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        processRound(roundEnv)

        if (roundEnv.processingOver()) {
            processingOver()
        }

        return claimAnnotations(annotations, roundEnv)
    }

    abstract fun processRound(roundEnv: RoundEnvironment)

    abstract fun claimAnnotations(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean

    abstract fun processingOver()
}
