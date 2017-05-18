package com.airbnb.paris.annotations

import javax.lang.model.element.Element

fun Element.hasAnnotation(simpleName: String): Boolean {
    return this.annotationMirrors
            .map { it.annotationType.asElement().simpleName.toString() }
            .contains(simpleName)
}