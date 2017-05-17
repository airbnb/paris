package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Format
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

internal class AttrInfo private constructor(
        val enclosingElement: Element,
        val name: String,
        val format: Format,
        val id: Id,
        val isMethod: Boolean) {

    companion object {

        fun fromElement(resourceProcessor: ResourceProcessor, element: Element): AttrInfo {
            // TODO  Check that element is either a method or a field
            // TODO  Check that element isn't private or protected

            val enclosingElement = element.enclosingElement
            val name = element.simpleName.toString()
            val attr = element.getAnnotation(Attr::class.java)
            val styleableResourceValue = attr.value
            var format = attr.format
            val id = resourceProcessor.getId(Attr::class.java, element, styleableResourceValue)

            if (format == Format.DEFAULT) {
                val type: TypeMirror
                if (element.kind == ElementKind.FIELD) {
                    type = element.asType()
                } else {
                    type = (element as ExecutableElement).parameters[0].asType()
                }
                format = Format.fromType(type)
            }

            val isMethod = element.kind == ElementKind.METHOD

            return AttrInfo(enclosingElement, name, format, id, isMethod)
        }
    }
}