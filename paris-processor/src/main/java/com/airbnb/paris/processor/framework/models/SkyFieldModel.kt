package com.airbnb.paris.processor.framework.models

import com.airbnb.paris.processor.framework.isJava
import com.airbnb.paris.processor.framework.isKotlin
import com.airbnb.paris.processor.framework.siblings
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

abstract class SkyFieldModel(
    val enclosingElement: TypeElement,
    val element: VariableElement,
    val type: TypeMirror,
    val name: String
) : SkyModel {

    /**
     * Null if [isKotlin] is false
     */
    val kotlinGetterElement: ExecutableElement?

    protected constructor(element: Element) : this(
        element.enclosingElement as TypeElement,
        element as VariableElement,
        element.asType(),
        element.simpleName.toString()
    )

    init {
        kotlinGetterElement = if (isKotlin()) {
            val getterName = "get${element.simpleName.toString().capitalize()}"
            val getter = element.siblings().asSequence()
                .filter {
                    it is ExecutableElement && it.simpleName.toString() == getterName
                }
                .firstOrNull()

            if (getter != null) {
                getter as ExecutableElement
            } else {
                null
            }
        } else {
            null
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

abstract class SkyFieldModelFactory<T : SkyFieldModel>(annotationClass: Class<out Annotation>) :
    SkyModelFactory<T, VariableElement>(annotationClass)

