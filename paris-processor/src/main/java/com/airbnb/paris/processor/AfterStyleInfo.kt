package com.airbnb.paris.processor

import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.check
import com.airbnb.paris.processor.utils.toTypeMirror
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

internal class AfterStyleInfo private constructor(
        val enclosingElement: Element,
        val elementName: String) {

    companion object {

        fun fromEnvironment(p: ParisProcessor, roundEnv: RoundEnvironment): List<AfterStyleInfo> {
            return roundEnv.getElementsAnnotatedWith(AfterStyle::class.java)
                    .mapNotNull {
                        try {
                            fromElement(p, it as ExecutableElement)
                        } catch (e: ProcessorException) {
                            Errors.log(e)
                            null
                        }
                    }
        }

        @Throws(ProcessorException::class)
        private fun fromElement(p: ParisProcessor, element: ExecutableElement): AfterStyleInfo {
            check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
                "Methods annotated with @AfterStyle can't be private or protected"
            }

            val styleType = ParisProcessor.STYLE_CLASS_NAME.toTypeMirror(p.elementUtils)
            val parameterType = element.parameters[0].asType()
            check(element.parameters.size == 1 && p.typeUtils.isSameType(styleType, parameterType)) {
                "Methods annotated with @AfterStyle must have a single Style parameter"
            }

            val enclosingElement = element.enclosingElement
            val elementName = element.simpleName.toString()

            return AfterStyleInfo(enclosingElement, elementName)
        }
    }
}
