package com.airbnb.paris.processor.framework

import com.squareup.javapoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement


// Element

internal fun Element.isPublic(): Boolean = this.modifiers.contains(Modifier.PUBLIC)
internal fun Element.isNotPublic(): Boolean = !isPublic()

internal fun Element.isPrivate(): Boolean = this.modifiers.contains(Modifier.PRIVATE)
internal fun Element.isNotPrivate(): Boolean = !isPrivate()

internal fun Element.isProtected(): Boolean = this.modifiers.contains(Modifier.PROTECTED)
internal fun Element.isNotProtected(): Boolean = !isProtected()

internal fun Element.isStatic(): Boolean = Modifier.STATIC in modifiers
internal fun Element.isNotStatic(): Boolean = !isStatic()

internal fun Element.isFinal(): Boolean = Modifier.FINAL in modifiers
internal fun Element.isNotFinal(): Boolean = !isFinal()

internal fun Element.isClass(): Boolean = kind == ElementKind.CLASS
internal fun Element.isNotClass(): Boolean = !isClass()

internal fun Element.isField(): Boolean = kind == ElementKind.FIELD
internal fun Element.isNotField(): Boolean = !isField()

internal fun Element.isMethod(): Boolean = kind == ElementKind.METHOD
internal fun Element.isNotMethod(): Boolean = !isMethod()

internal fun Element.hasAnnotation(simpleName: String): Boolean {
    return this.annotationMirrors
        .map { it.annotationType.asElement().simpleName.toString() }
        .contains(simpleName)
}

internal fun Element.hasAnyAnnotation(simpleNames: Set<String>): Boolean {
    return this.annotationMirrors
        .map { it.annotationType.asElement().simpleName.toString() }
        .any { simpleNames.contains(it) }
}

fun Element.toStringId(): String {
    return when (this) {
        is PackageElement -> qualifiedName.toString()
        is TypeElement -> qualifiedName.toString()
        is ExecutableElement,
        is VariableElement -> "${enclosingElement.toStringId()}.$simpleName"
        else -> simpleName.toString()
    }
}

internal val KOTLIN_METADATA_ANNOTATION =
    Class.forName("kotlin.Metadata").asSubclass(Annotation::class.java)

/**
 * True is [isJava] is false and vice-versa
 */
internal fun Element.isKotlin(): Boolean = when (this) {
    is TypeElement -> getAnnotation(KOTLIN_METADATA_ANNOTATION) != null
    is ExecutableElement, is VariableElement -> enclosingElement.isKotlin()
    else -> TODO()
}

/**
 * True is [isKotlin] is false and vice-versa
 */
internal fun Element.isJava(): Boolean = !isKotlin()

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

internal val TypeElement.packageName: String get() = className.packageName()

