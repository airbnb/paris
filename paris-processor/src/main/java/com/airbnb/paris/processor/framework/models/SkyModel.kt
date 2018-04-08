package com.airbnb.paris.processor.framework.models

import com.airbnb.paris.processor.framework.errors.Errors
import com.airbnb.paris.processor.framework.errors.ProcessorException
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

interface SkyModel

internal abstract class SkyModelFactory<T : SkyModel, in E : Element>(
        private val annotationClass: Class<out Annotation>
) {

    var models = emptyList<T>()
        private set

    var latest = emptyList<T>()
        private set

    fun process(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(annotationClass)
                .mapNotNull {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        elementToModel(it as E)
                    } catch (e: ProcessorException) {
                        Errors.log(e)
                        null
                    }
                }
                .let {
                    models += it
                    latest = it
                }
    }

    abstract fun elementToModel(element: E): T?
}
