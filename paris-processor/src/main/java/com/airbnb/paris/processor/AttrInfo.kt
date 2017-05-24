package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.check
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Element  The annotated element
 * Target   The method parameter
 */
internal class AttrInfo private constructor(
        val enclosingElement: Element,
        val targetFormat: Format,
        val elementName: String,
        val styleableResId: AndroidResourceId,
        val defaultValueResId: AndroidResourceId?) {

    companion object {

        fun fromEnvironment(roundEnv: RoundEnvironment, elementUtils: Elements, typeUtils: Types, resourceScanner: AndroidResourceScanner): List<AttrInfo> {
            return roundEnv.getElementsAnnotatedWith(Attr::class.java)
                    .mapNotNull {
                        try {
                            fromElement(elementUtils, typeUtils, resourceScanner, it as ExecutableElement)
                        } catch (e: ProcessorException) {
                            Errors.log(e)
                            null
                        }
                    }
        }

        @Throws(ProcessorException::class)
        private fun fromElement(elementUtils: Elements, typeUtils: Types, resourceScanner: AndroidResourceScanner, element: ExecutableElement): AttrInfo {
            check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
                "Methods annotated with @Attr can't be private or protected"
            }

            val attr = element.getAnnotation(Attr::class.java)

            val enclosingElement = element.enclosingElement

            val targetFormat = Format.forElement(elementUtils, typeUtils, element)

            val elementName = element.simpleName.toString()

            val styleableResId = resourceScanner.getId(Attr::class.java, element, attr.value)
            var defaultValueResId: AndroidResourceId? = null
            if (attr.defaultValue != -1) {
                defaultValueResId = resourceScanner.getId(Attr::class.java, element, attr.defaultValue)
            }

            return AttrInfo(
                    enclosingElement,
                    targetFormat,
                    elementName,
                    styleableResId,
                    defaultValueResId)
        }
    }
}
