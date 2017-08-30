package com.airbnb.paris.processor.utils

import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements

fun ClassName.toTypeElement(elementUtils: Elements): TypeElement =
        elementUtils.getTypeElement(reflectionName())

fun ClassName.toTypeMirror(elementUtils: Elements): TypeMirror =
        toTypeElement(elementUtils).asType()
