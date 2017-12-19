package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.type.*

abstract class SkyHelper<out T : SkyProcessor>(val processor: T) {

    val filer = processor.filer
    val messager = processor.messager
    val elements = processor.elements
    val types = processor.types

    protected fun isSameType(type1: TypeMirror, type2: TypeMirror) = types.isSameType(type1, type2)

    protected fun isSubtype(type1: TypeMirror, type2: TypeMirror) = types.isSubtype(type1, type2)

    protected fun erasure(type: TypeMirror): TypeMirror = types.erasure(type)

    protected fun ClassName.toTypeMirror(): TypeMirror = toTypeElement(elements).asType()

    protected fun TypeMirror.asTypeElement(): TypeElement = this.asTypeElement(types)

    protected fun Element.getPackageElement(): PackageElement = elements.getPackageOf(this)
}
