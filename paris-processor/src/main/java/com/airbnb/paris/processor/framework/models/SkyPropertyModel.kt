package com.airbnb.paris.processor.framework.models

import com.airbnb.paris.processor.framework.isJava
import com.airbnb.paris.processor.framework.isKotlin
import com.airbnb.paris.processor.framework.siblings
import com.airbnb.paris.processor.framework.toStringId
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

/**
 * Applies to Java fields and Kotlin properties
 */
abstract class SkyPropertyModel(val element: Element) : SkyModel {

    val enclosingElement: TypeElement = element.enclosingElement as TypeElement
    val type: TypeMirror
    val name: String

    /**
     * Null if [isKotlin] is false
     */
    val kotlinGetterElement: ExecutableElement?

    init {
        if (element.isJava()) {
            type = element.asType()
            name = element.simpleName.toString()
            kotlinGetterElement = null
        } else {
            // In Kotlin it's an empty static method whose name is <property>$annotations that ends
            // up being annotated
            name = element.simpleName.toString().substringBefore("\$annotations")
            val getterName = "get${name.capitalize()}"
            kotlinGetterElement = element.siblings().asSequence()
                .filter {
                    it is ExecutableElement &&
                            it.simpleName.toString() == getterName &&
                            it.parameters.isEmpty()
                }
                .singleOrNull() as ExecutableElement?

            if (kotlinGetterElement == null) {
                throw IllegalArgumentException("${element.toStringId()}: Could not find getter for property annotated with @StyleableChild. This probably means the property is private.")
            }

            type = kotlinGetterElement.returnType
        }
    }

    /**
     * True is [isJava] is false and vice-versa
     */
    fun isKotlin(): Boolean = element.isKotlin()

    /**
     * True is [isKotlin] is false and vice-versa
     */
    fun isJava(): Boolean = element.isJava()
}

abstract class SkyFieldModelFactory<T : SkyPropertyModel>(annotationClass: Class<out Annotation>) :
    SkyModelFactory<T, Element>(annotationClass)
