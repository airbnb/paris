package com.airbnb.paris.processor.framework

import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.*

internal fun String.toClassNameObject() =
        ClassName.get(this.substringBeforeLast("."), this.substringAfterLast("."))

internal fun ClassName.toTypeElement(elementUtils: Elements): TypeElement =
        elementUtils.getTypeElement(reflectionName())

internal fun ClassName.toTypeMirror(elementUtils: Elements): TypeMirror =
        toTypeElement(elementUtils).asType()
