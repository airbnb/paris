package com.airbnb.paris.processor.framework.models

import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

interface SkyModel

abstract class SkyModelFactory<T : SkyModel, in E : Element>(
    private val annotationClass: Class<out Annotation>
) {

    var models = emptyList<T>()
        private set

    var latest = emptyList<T>()
        private set

    fun process(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(annotationClass)
            .filter { filter(it) }
            .mapNotNull {
                @Suppress("UNCHECKED_CAST")
                elementToModel(it as E)
            }
            .let {
                models += it
                latest = it
            }
    }

    open fun filter(element: Element): Boolean = true

    abstract fun elementToModel(element: E): T?
}
