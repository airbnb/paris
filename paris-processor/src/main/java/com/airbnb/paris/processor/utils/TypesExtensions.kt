package com.airbnb.paris.processor.utils

import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

fun Types.isView(elementUtils: Elements, type: TypeMirror): Boolean =
        this.isSubtype(type, elementUtils.VIEW_TYPE.asType())
