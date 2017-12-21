package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.errors.*
import com.airbnb.paris.processor.framework.models.*
import javax.lang.model.element.*

internal class AfterStyleInfoExtractor
    : SkyMethodModelFactory<AfterStyleInfo>(AfterStyle::class.java) {

    override fun elementToModel(element: ExecutableElement): AfterStyleInfo? {
        check(element.isNotPrivate() && element.isNotProtected(), element) {
            "Methods annotated with @AfterStyle can't be private or protected"
        }

        val styleType = STYLE_CLASS_NAME.toTypeMirror()
        val parameterType = element.parameters[0].asType()
        check(element.parameters.size == 1 && isSameType(styleType, parameterType)) {
            "Methods annotated with @AfterStyle must have a single Style parameter"
        }

        return AfterStyleInfo(element)
    }
}

internal class AfterStyleInfo(element: ExecutableElement) : SkyMethodModel(element)

