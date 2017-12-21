package com.airbnb.paris.processor.framework

import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.type.*

internal val filer get() = SkyProcessor.INSTANCE.filer
internal val messager get() = SkyProcessor.INSTANCE.messager
internal val elements get() = SkyProcessor.INSTANCE.elements
internal val types get() = SkyProcessor.INSTANCE.types

internal fun erasure(type: TypeMirror): TypeMirror = types.erasure(type)

internal fun isSameType(type1: TypeMirror, type2: TypeMirror) = types.isSameType(type1, type2)

internal fun isSubtype(type1: TypeMirror, type2: TypeMirror) = types.isSubtype(type1, type2)

// ClassName

internal fun ClassName.toTypeElement(): TypeElement = elements.getTypeElement(reflectionName())

internal fun ClassName.toTypeMirror(): TypeMirror = toTypeElement().asType()

// Element

internal fun Element.getPackageElement(): PackageElement = elements.getPackageOf(this)

internal fun Element.isPrivate(): Boolean = this.modifiers.contains(Modifier.PRIVATE)
internal fun Element.isNotPrivate(): Boolean = !isPrivate()

internal fun Element.isProtected(): Boolean = this.modifiers.contains(Modifier.PROTECTED)
internal fun Element.isNotProtected(): Boolean = !isProtected()

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

// String

internal fun String.className(): ClassName =
        ClassName.get(this.substringBeforeLast("."), this.substringAfterLast("."))

// TypeElement

internal val TypeElement.className: ClassName get() = ClassName.get(this)

internal val TypeElement.packageName: String get() = className.packageName()

// TypeMirror

internal fun TypeMirror.asTypeElement(): TypeElement = types.asElement(this) as TypeElement

// Android specific

internal fun isView(type: TypeMirror): Boolean = isSubtype(type, AndroidClassNames.VIEW.toTypeMirror())

