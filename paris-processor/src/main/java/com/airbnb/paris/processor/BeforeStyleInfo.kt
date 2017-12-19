package com.airbnb.paris.processor

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.utils.*
import javax.annotation.processing.*
import javax.lang.model.element.*

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

            val styleType = STYLE_CLASS_NAME.toTypeMirror(p.elementUtils)
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
