package com.airbnb.paris.processor

import javax.lang.model.element.Element

fun Element.hasAnnotation(simpleName: String): Boolean {
    return this.annotationMirrors
            .map { it.annotationType.asElement().simpleName.toString() }
            .contains(simpleName)
}

fun Element.hasAnyAnnotation(simpleNames: Set<String>): Boolean {
    return this.annotationMirrors
            .map { it.annotationType.asElement().simpleName.toString() }
            .any { simpleNames.contains(it) }
}