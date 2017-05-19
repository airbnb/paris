package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Format
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class AttrInfo private constructor(
        val enclosingElement: Element,
        val type: TypeMirror,
        val name: String,
        val format: Format,
        val id: Id,
        val isMethod: Boolean,
        val isView: Boolean) {

    companion object {

        fun fromElement(resourceProcessor: ResourceProcessor, elementUtils: Elements, typeUtils: Types, element: Element): AttrInfo {
            // TODO  Check that element is either a method or a field
            // TODO  Check that element isn't private or protected

            val enclosingElement = element.enclosingElement
            val name = element.simpleName.toString()
            val attr = element.getAnnotation(Attr::class.java)
            val styleableResourceValue = attr.value
            var format = attr.format
            val id = resourceProcessor.getId(Attr::class.java, element, styleableResourceValue)

            var isView = false
            if (format == Format.DEFAULT) {
                if (element.kind == ElementKind.FIELD) {
                    format = Format.forField(elementUtils, typeUtils, element)
                } else {
                    format = Format.forMethod(element)
                }
            }

            val type: TypeMirror
            if (element.kind == ElementKind.FIELD) {
                type = element.asType()
                val viewType = elementUtils.getTypeElement("android.view.View").asType()
                isView = typeUtils.isSubtype(type, viewType)
            } else {
                type = (element as ExecutableElement).parameters[0].asType()
            }

            val isMethod = element.kind == ElementKind.METHOD

            return AttrInfo(enclosingElement, type, name, format, id, isMethod, isView)
        }
    }
}