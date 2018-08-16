package com.airbnb.paris.processor.framework.models

import com.airbnb.paris.processor.framework.*
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
     * The getter could be a field or a method depending on if the annotated class is in Java or in Kotlin
     */
    val getterElement: Element

    /**
     * What you'd call to get the property value
     */
    val getter: String

    init {
        if (element.isJava()) {
            type = element.asType()
            name = element.simpleName.toString()
            getterElement = element
            getter = name
        } else {
            // In Kotlin it's an empty static method whose name is <property>$annotations that ends
            // up being annotated
            name = element.simpleName.toString().substringBefore("\$annotations")
            val getterName = "get${name.capitalize()}"
            val kotlinGetterElement = element.siblings().asSequence()
                .filter {
                    it is ExecutableElement &&
                            it.simpleName.toString() == getterName &&
                            it.parameters.isEmpty()
                }
                .singleOrNull() as ExecutableElement?

            kotlinGetterElement ?: throw IllegalArgumentException("${element.toStringId()}: Could not find getter for property annotated with @StyleableChild. This probably means the property is private.")

            getterElement = kotlinGetterElement
            getter = "${kotlinGetterElement.simpleName}()"

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

typealias SkyFieldModel = SkyPropertyModel

abstract class SkyFieldModelFactory<T : SkyPropertyModel>(
    processor: SkyProcessor,
    annotationClass: Class<out Annotation>
) : SkyModelFactory<T, Element>(processor, annotationClass)
