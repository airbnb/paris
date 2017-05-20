package com.airbnb.paris.processor

import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement

val TypeElement.className get() = ClassName.get(this)

val TypeElement.packageName get() = className.packageName()

