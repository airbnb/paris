package com.airbnb.paris.processor.models

import androidx.room.compiler.processing.XMethodElement
import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.framework.models.SkyMethodModel
import com.airbnb.paris.processor.framework.models.SkyMethodModelFactory
import com.airbnb.paris.processor.utils.isSameTypeName

internal class AfterStyleInfoExtractor(val parisProcessor: ParisProcessor) : SkyMethodModelFactory<AfterStyleInfo>(parisProcessor, AfterStyle::class.java) {

    override fun elementToModel(element: XMethodElement): AfterStyleInfo? {

        if (element.isPrivate() || element.isProtected()) {
            parisProcessor.logError(element) {
                "Methods annotated with @AfterStyle can't be private or protected."
            }
            return null
        }

        val parameterType = element.parameters.firstOrNull()?.type

        if (parameterType == null || !parameterType.isSameTypeName(STYLE_CLASS_NAME)) {
            parisProcessor.logError(element) {
                "Methods annotated with @AfterStyle must have a single Style parameter."
            }
            return null
        }

        return AfterStyleInfo(element)
    }
}

internal class AfterStyleInfo(element: XMethodElement) : SkyMethodModel(element)
