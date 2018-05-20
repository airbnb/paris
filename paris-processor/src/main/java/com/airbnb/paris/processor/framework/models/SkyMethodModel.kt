package com.airbnb.paris.processor.framework.models

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
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

typealias SkyStaticMethodModel = SkyMethodModel

abstract class SkyMethodModelFactory<T : SkyMethodModel>(
    annotationClass: Class<out Annotation>
) : SkyModelFactory<T, ExecutableElement>(annotationClass) {

    override fun filter(element: Element): Boolean = element.kind == ElementKind.METHOD
}

typealias SkyStaticMethodModelFactory<T> = SkyMethodModelFactory<T>
