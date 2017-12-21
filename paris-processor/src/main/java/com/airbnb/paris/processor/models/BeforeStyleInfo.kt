package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.utils.*
import javax.lang.model.element.*

internal class BeforeStyleInfoExtractor
    : SkyMethodModelFactory<BeforeStyleInfo>(BeforeStyle::class.java) {

    override fun elementToModel(element: ExecutableElement): BeforeStyleInfo? {
        check(element.isNotPrivate() && element.isNotProtected(), element) {
            "Methods annotated with @BeforeStyle can't be private or protected"
        }

        val styleType = STYLE_CLASS_NAME.toTypeMirror()
        val parameterType = element.parameters[0].asType()
        check(element.parameters.size == 1 && isSameType(styleType, parameterType)) {
            "Methods annotated with @BeforeStyle must have a single Style parameter"
        }

        return BeforeStyleInfo(element)
    }
}

internal class BeforeStyleInfo(element: ExecutableElement) : SkyMethodModel(element)

