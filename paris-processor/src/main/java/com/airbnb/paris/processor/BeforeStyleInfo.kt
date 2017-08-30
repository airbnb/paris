package com.airbnb.paris.processor

import com.airbnb.paris.annotations.BeforeStyle
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.check
import com.airbnb.paris.processor.utils.toTypeMirror
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

internal class BeforeStyleInfo private constructor(
        val enclosingElement: Element,
        val elementName: String) {

    companion object {

        fun fromEnvironment(p: ParisProcessor, roundEnv: RoundEnvironment): List<BeforeStyleInfo> {
            return roundEnv.getElementsAnnotatedWith(BeforeStyle::class.java)
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
        private fun fromElement(p: ParisProcessor, element: ExecutableElement): BeforeStyleInfo {
            check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
                "Methods annotated with @BeforeStyle can't be private or protected"
            }

            val styleType = ParisProcessor.STYLE_CLASS_NAME.toTypeMirror(p.elementUtils)
            val parameterType = element.parameters[0].asType()
            check(element.parameters.size == 1 && p.typeUtils.isSameType(styleType, parameterType)) {
                "Methods annotated with @BeforeStyle must have a single Style parameter"
            }

            val enclosingElement = element.enclosingElement
            val elementName = element.simpleName.toString()

            return BeforeStyleInfo(enclosingElement, elementName)
        }
    }
}
