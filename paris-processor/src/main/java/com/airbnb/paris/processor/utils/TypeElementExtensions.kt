package com.airbnb.paris.processor.utils

import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement

val TypeElement.className: ClassName get() = ClassName.get(this)

val TypeElement.packageName: String get() = className.packageName()

