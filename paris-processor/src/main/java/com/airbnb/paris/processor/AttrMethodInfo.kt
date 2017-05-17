package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Format
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

internal class AttrMethodInfo private constructor(val enclosingElement: Element, val name: String, val format: Format, val id: Id) {

    companion object {

        fun fromElement(resourceProcessor: ResourceProcessor, element: ExecutableElement): AttrMethodInfo {
            val enclosingElement = element.enclosingElement
            val name = element.simpleName.toString()
            val type = element.asType()
            val attr = element.getAnnotation(Attr::class.java)
            val styleableResourceValue = attr.value
            val id = resourceProcessor.getId(Attr::class.java, element, styleableResourceValue)

            val parameterType = element.parameters[0].asType()
            val format = Format.fromType(parameterType)

            return AttrMethodInfo(enclosingElement, name, format, id)
        }
    }
}