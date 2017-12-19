package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.type.*

internal class AttrInfoExtractor(processor: ParisProcessor) : ParisHelper(processor) {

    fun fromEnvironment(roundEnv: RoundEnvironment): List<AttrInfo> {
        return roundEnv.getElementsAnnotatedWith(Attr::class.java)
                .mapNotNull {
                    try {
                        fromElement(it as ExecutableElement)
                    } catch (e: ProcessorException) {
                        Errors.log(e)
                        null
                    }
                }
    }

    @Throws(ProcessorException::class)
    private fun fromElement(element: ExecutableElement): AttrInfo {
        check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
            "Methods annotated with @Attr can't be private or protected"
        }

        val attr = element.getAnnotation(Attr::class.java)

        val enclosingElement = element.enclosingElement as TypeElement

        val targetType = element.parameters[0].asType()

        val targetFormat = Format.forElement(elements, types, element)

        val elementName = element.simpleName.toString()

        val styleableResId = getResourceId(Attr::class.java, element, attr.value)
        var defaultValueResId: AndroidResourceId? = null
        if (attr.defaultValue != -1) {
            defaultValueResId = getResourceId(Attr::class.java, element, attr.defaultValue)
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

/**
 * Element  The annotated element
 * Target   The method parameter
 */
internal data class AttrInfo(
        val enclosingElement: TypeElement,
        val targetType: TypeMirror,
        val targetFormat: Format,
        val elementName: String,
        val styleableResId: AndroidResourceId,
        val defaultValueResId: AndroidResourceId?,
        val javadoc: CodeBlock)
