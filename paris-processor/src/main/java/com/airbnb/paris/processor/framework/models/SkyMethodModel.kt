package com.airbnb.paris.processor.framework.models

import com.airbnb.paris.processor.abstractions.XElement
import com.airbnb.paris.processor.abstractions.XExecutableElement
import com.airbnb.paris.processor.abstractions.XTypeElement
import com.airbnb.paris.processor.abstractions.isMethod
import com.airbnb.paris.processor.framework.JavaSkyProcessor
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

abstract class SkyMethodModel private constructor(
    val enclosingElement: XTypeElement,
    val element: XExecutableElement,
) : SkyModel {
    val name: String get() = element.name

    protected constructor(element: XExecutableElement) : this(
        element.enclosingTypeElement,
        element
    )
}

typealias SkyStaticMethodModel = SkyMethodModel

abstract class SkyMethodModelFactory<T : SkyMethodModel>(
    processor: JavaSkyProcessor,
    annotationClass: Class<out Annotation>
) : JavaSkyModelFactory<T, XExecutableElement>(processor, annotationClass) {

    override fun filter(element: XElement): Boolean = element.isMethod()
}

typealias SkyStaticMethodModelFactory<T> = SkyMethodModelFactory<T>
