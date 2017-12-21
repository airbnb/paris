package com.airbnb.paris.processor.framework.models

import javax.lang.model.element.*
import javax.lang.model.type.*

internal abstract class SkyFieldModel(
        val enclosingElement: TypeElement,
        val element: Element,
        val type: TypeMirror,
        val name: String
) : SkyModel {

    protected constructor(element: Element): this(
            element.enclosingElement as TypeElement,
            element,
            element.asType(),
            element.simpleName.toString()
    )
}

internal abstract class SkyFieldModelFactory<out T : SkyFieldModel>(annotationClass: Class<out Annotation>)
    : SkyModelFactory<T, Element>(annotationClass)

