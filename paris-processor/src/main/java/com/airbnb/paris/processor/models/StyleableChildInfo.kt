package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.utils.*
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.type.*

internal class StyleableChildInfoExtractor(processor: ParisProcessor) : ParisHelper(processor) {

    fun fromEnvironment(roundEnv: RoundEnvironment): List<StyleableChildInfo> {
        return roundEnv.getElementsAnnotatedWith(StyleableChild::class.java)
                .mapNotNull {
                    try {
                        fromElement(it)
                    } catch (e: ProcessorException) {
                        Errors.log(e)
                        null
                    }
                }
    }

    @Throws(ProcessorException::class)
    private fun fromElement(element: Element): StyleableChildInfo {
        check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
            "Fields annotated with @StyleableChild can't be private or protected"
        }

        val attr = element.getAnnotation(StyleableChild::class.java)

        val enclosingElement = element.enclosingElement as TypeElement

        val elementType = element.asType()

        val elementName = element.simpleName.toString()

        val styleableResId = getResourceId(Attr::class.java, element, attr.value)
        var defaultValueResId: AndroidResourceId? = null
        if (attr.defaultValue != -1) {
            defaultValueResId = getResourceId(Attr::class.java, element, attr.defaultValue)
        }

        return StyleableChildInfo(
                enclosingElement,
                elementType,
                elementName,
                styleableResId,
                defaultValueResId)
    }
}

internal data class StyleableChildInfo(
        val enclosingElement: TypeElement,
        val elementType: TypeMirror,
        val elementName: String,
        val styleableResId: AndroidResourceId,
        val defaultValueResId: AndroidResourceId?
)
