package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.utils.*
import javax.annotation.processing.*
import javax.lang.model.element.*

internal class AfterStyleInfoExtractor(processor: ParisProcessor) : ParisHelper(processor) {

    fun fromEnvironment(roundEnv: RoundEnvironment): List<AfterStyleInfo> {
        return roundEnv.getElementsAnnotatedWith(AfterStyle::class.java)
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
    private fun fromElement(element: ExecutableElement): AfterStyleInfo {
        check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
            "Methods annotated with @AfterStyle can't be private or protected"
        }

        val styleType = STYLE_CLASS_NAME.toTypeMirror()
        val parameterType = element.parameters[0].asType()
        check(element.parameters.size == 1 && isSameType(styleType, parameterType)) {
            "Methods annotated with @AfterStyle must have a single Style parameter"
        }

        val enclosingElement = element.enclosingElement
        val elementName = element.simpleName.toString()

        return AfterStyleInfo(enclosingElement, elementName)
    }
}

internal data class AfterStyleInfo(
        val enclosingElement: Element,
        val elementName: String
)
