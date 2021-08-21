package com.airbnb.paris.processor.framework.models

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XRoundEnv
import com.airbnb.paris.processor.framework.JavaSkyProcessor
import com.airbnb.paris.processor.framework.WithJavaSkyProcessor

interface SkyModel

abstract class JavaSkyModelFactory<T : SkyModel, in E : XElement>(
    override val processor: JavaSkyProcessor,
    private val annotationClass: Class<out Annotation>
) : WithJavaSkyProcessor {

    var models = emptyList<T>()
        private set

    var latest = emptyList<T>()
        private set

    fun process(roundEnv: XRoundEnv) {
        roundEnv.getElementsAnnotatedWith(annotationClass.canonicalName)
            .filter(::filter)
            .mapNotNull {
                @Suppress("UNCHECKED_CAST")
                elementToModel(it as E)
            }
            .let {
                models += it
                latest = it
            }
    }

    open fun filter(element: XElement): Boolean = true

    abstract fun elementToModel(element: E): T?
}
