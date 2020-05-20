package com.airbnb.paris.processor.framework

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

abstract class SkyProcessor : AbstractProcessor(), WithSkyProcessor {

    override val filer: Filer by lazy { processingEnv.filer }
    override val messager: Messager by lazy { processingEnv.messager }
    override val elements: Elements by lazy { processingEnv.elementUtils }
    override val types: Types by lazy { processingEnv.typeUtils }

    override val memoizer: SkyMemoizer by lazy { SkyMemoizer(this) }
    override val loggedMessages = mutableListOf<Message>()

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
