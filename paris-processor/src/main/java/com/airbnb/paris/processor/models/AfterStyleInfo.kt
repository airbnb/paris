package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.abstractions.XExecutableElement
import com.airbnb.paris.processor.abstractions.javac.JavacExecutableElement
import com.airbnb.paris.processor.framework.isPrivate
import com.airbnb.paris.processor.framework.isProtected
import com.airbnb.paris.processor.framework.models.SkyMethodModel
import com.airbnb.paris.processor.framework.models.SkyMethodModelFactory
import javax.lang.model.element.ExecutableElement

internal class AfterStyleInfoExtractor(override val processor: ParisProcessor) : SkyMethodModelFactory<AfterStyleInfo>(processor, AfterStyle::class.java) {

    override fun elementToModel(element: XExecutableElement): AfterStyleInfo? {

        if (element.isPrivate() || element.isProtected()) {
            logError(element) {
                "Methods annotated with @AfterStyle can't be private or protected."
            }
            return null
        }

        val parameterType = element.parameters.firstOrNull()?.type

        if (parameterType == null || !parameterType.isSameTypeName(STYLE_CLASS_NAME)) {
            logError(element) {
                "Methods annotated with @AfterStyle must have a single Style parameter."
            }
            return null
        }

        return AfterStyleInfo(element)
    }
}

internal class AfterStyleInfo(element: XExecutableElement) : SkyMethodModel(element)
