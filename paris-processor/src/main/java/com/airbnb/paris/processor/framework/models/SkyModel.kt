package com.airbnb.paris.processor.framework.models

import com.airbnb.paris.processor.framework.errors.*
import javax.annotation.processing.*
import javax.lang.model.element.*

interface SkyModel

internal abstract class SkyModelFactory<out T : SkyModel, in E : Element>(private val annotationClass: Class<out Annotation>) {

    fun extract(roundEnv: RoundEnvironment): List<T> {
        return roundEnv.getElementsAnnotatedWith(annotationClass)
                .mapNotNull {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        elementToModel(it as E)
                    } catch (e: ProcessorException) {
                        Errors.log(e)
                        null
                    }
                }
    }

    abstract fun elementToModel(element: E): T?
}
