package com.airbnb.paris.processor.framework.models

import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

abstract class SkyMethodModel private constructor(
    val enclosingElement: TypeElement,
    val element: ExecutableElement,
    val name: String
) : SkyModel {

    protected constructor(element: ExecutableElement) : this(
        element.enclosingElement as TypeElement,
        element,
        element.simpleName.toString()
    )
}

abstract class SkyMethodModelFactory<T : SkyMethodModel>(
    annotationClass: Class<out Annotation>
) : SkyModelFactory<T, ExecutableElement>(annotationClass)

