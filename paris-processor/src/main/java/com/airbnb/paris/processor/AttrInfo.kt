package com.airbnb.paris.processor

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.*

/**
 * Element  The annotated element
 * Target   The method parameter
 */
internal class AttrInfo private constructor(
        val enclosingElement: Element,
        val targetType: TypeMirror,
        val targetFormat: Format,
        val elementName: String,
        val styleableResId: AndroidResourceId,
        val defaultValueResId: AndroidResourceId?,
        val javadoc: CodeBlock) {

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

            val targetType = element.parameters[0].asType()

            val targetFormat = Format.forElement(elementUtils, typeUtils, element)

            val elementName = element.simpleName.toString()

            val styleableResId = resourceScanner.getId(Attr::class.java, element, attr.value)
            var defaultValueResId: AndroidResourceId? = null
            if (attr.defaultValue != -1) {
                defaultValueResId = resourceScanner.getId(Attr::class.java, element, attr.defaultValue)
            }

            val javadoc = CodeBlock.of("@see \$T#\$N(\$T)", enclosingElement, elementName, targetType)

            return AttrInfo(
                    enclosingElement,
                    targetType,
                    targetFormat,
                    elementName,
                    styleableResId,
                    defaultValueResId,
                    javadoc)
        }
    }
}
