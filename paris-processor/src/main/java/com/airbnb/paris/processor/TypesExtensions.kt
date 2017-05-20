package com.airbnb.paris.processor

import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

fun Types.isView(elementUtils: Elements, type: TypeMirror): Boolean {
    return this.isSubtype(type, elementUtils.VIEW_TYPE.asType())
}
