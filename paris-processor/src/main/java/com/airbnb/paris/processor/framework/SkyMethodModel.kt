package com.airbnb.paris.processor.framework

import javax.lang.model.element.*

internal abstract class SkyMethodModel private constructor(
        val enclosingElement: TypeElement,
        val element: ExecutableElement,
        val name: String
) : SkyModel {

    protected constructor(element: ExecutableElement): this(
            element.enclosingElement as TypeElement,
            element,
            element.simpleName.toString()
    )
}

internal abstract class SkyMethodModelFactory<out T : SkyMethodModel>(annotationClass: Class<out Annotation>)
    : SkyModelFactory<T, ExecutableElement>(annotationClass)

