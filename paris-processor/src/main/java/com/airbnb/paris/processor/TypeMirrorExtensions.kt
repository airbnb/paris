package com.airbnb.paris.processor

import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types

fun TypeMirror.asTypeElement(typeUtils: Types): TypeElement {
    return typeUtils.asElement(this) as TypeElement
}