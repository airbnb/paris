package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.BeforeStyle
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.framework.isPrivate
import com.airbnb.paris.processor.framework.isProtected
import com.airbnb.paris.processor.framework.models.SkyMethodModel
import com.airbnb.paris.processor.framework.models.SkyMethodModelFactory
import javax.lang.model.element.ExecutableElement

internal class BeforeStyleInfoExtractor(override val processor: ParisProcessor) : SkyMethodModelFactory<BeforeStyleInfo>(processor, BeforeStyle::class.java) {

    override fun elementToModel(element: ExecutableElement): BeforeStyleInfo? {
        if (element.isPrivate() || element.isProtected()) {
            logError(element) {
                "Methods annotated with @BeforeStyle can't be private or protected."
            }
            return null
        }


        val parameterType = element.parameters.firstOrNull()?.asType()
        if (parameterType == null || !isSameType(processor.memoizer.styleClassType, parameterType)) {
            logError(element) {
                "Methods annotated with @BeforeStyle must have a single Style parameter."
            }
            return null
        }

        return BeforeStyleInfo(element)
    }
}

internal class BeforeStyleInfo(element: ExecutableElement) : SkyMethodModel(element)

