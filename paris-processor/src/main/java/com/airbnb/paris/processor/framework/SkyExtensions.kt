package com.airbnb.paris.processor.framework

import com.squareup.javapoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement


internal fun Element.siblings(): List<Element> = when (this) {
    is ExecutableElement,
    is VariableElement -> enclosingElement.enclosedElements.filterNot { it === this }
    else -> TODO()
}

//internal fun XElement.siblings(): List<Element> = when (this) {
//    is XExecutableElement -> enclosingTypeElement.en
//    is XFieldElement -> enclosingElement.enclosedElements.filterNot { it === this }
//    else -> TODO()
//}

// String

internal fun String.className(): ClassName =
    ClassName.get(this.substringBeforeLast("."), this.substringAfterLast("."))

// TypeElement

internal val TypeElement.className: ClassName get() = ClassName.get(this)


