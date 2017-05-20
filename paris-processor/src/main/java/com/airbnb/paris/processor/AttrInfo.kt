package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Format
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Element  The annotated element
 * Target   The attribute recipient (either a field or a method parameter)
 */
internal class AttrInfo private constructor(
        private val enclosingElement: Element,
        val targetType: TypeMirror,
        val targetFormat: Format,
        val elementName: String,
        val isElementAMethod: Boolean,
        val isElementStyleable: Boolean,
        val styleableResId: AndroidResourceId,
        val defaultValueResId: AndroidResourceId?) {

    companion object {

        @Throws(ProcessorException::class)
        fun fromElement(resourceProcessor: ResourceProcessor, elementUtils: Elements, typeUtils: Types, element: Element): AttrInfo {

            check(element.kind == ElementKind.FIELD || element.kind == ElementKind.METHOD, element) {
                "Element annotated with @Attr must be a field or method"
            }
            check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
                "Fields and methods annotated with @Attr can't be private or protected"
            }

            val attr = element.getAnnotation(Attr::class.java)

            val enclosingElement = element.enclosingElement

            val targetType = if (element.kind == ElementKind.FIELD) {
                element.asType()
            } else {
                (element as ExecutableElement).parameters[0].asType()
            }
            var targetFormat = attr.format
            if (targetFormat == Format.DEFAULT) {
                // The format wasn't specified, use the context to guess at it
                targetFormat = Format.forElement(elementUtils, typeUtils, element)
            }

            val elementName = element.simpleName.toString()
            val isElementAMethod = element.kind == ElementKind.METHOD
            val isElementStyleable = element.kind == ElementKind.FIELD && typeUtils.isView(elementUtils, targetType)

            val styleableResId = resourceProcessor.getId(Attr::class.java, element, attr.value)
            var defaultValueResId: AndroidResourceId? = null
            if (attr.defaultValue != -1) {
                defaultValueResId = resourceProcessor.getId(Attr::class.java, element, attr.defaultValue)
            }

            return AttrInfo(
                    enclosingElement,
                    targetType,
                    targetFormat,
                    elementName,
                    isElementAMethod,
                    isElementStyleable,
                    styleableResId,
                    defaultValueResId)
        }
    }

    internal fun belongsTo(element: Element): Boolean {
        return element == enclosingElement
    }
}