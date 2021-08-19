package com.airbnb.paris.processor.models

import androidx.room.compiler.processing.XMethodElement
import com.airbnb.paris.annotations.BeforeStyle
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.framework.models.SkyMethodModel
import com.airbnb.paris.processor.framework.models.SkyMethodModelFactory

internal class BeforeStyleInfoExtractor(override val processor: ParisProcessor) : SkyMethodModelFactory<BeforeStyleInfo>(processor, BeforeStyle::class.java) {

    override fun elementToModel(element: XMethodElement): BeforeStyleInfo? {
        if (element.isPrivate() || element.isProtected()) {
            logError(element) {
                "Methods annotated with @BeforeStyle can't be private or protected."
            }
            return null
        }


        val parameterType = element.parameters.firstOrNull()?.type
        // TODO: 2/21/21 BeforeStyle doesn't seem tested in the project?!
        if (parameterType == null || processor.memoizer.styleClassTypeX.isAssignableFrom(parameterType)) {
            logError(element) {
                "Methods annotated with @BeforeStyle must have a single Style parameter."
            }
            return null
        }

        return BeforeStyleInfo(element)
    }
}

internal class BeforeStyleInfo(element: XMethodElement) : SkyMethodModel(element)

