package com.airbnb.paris.processor

import com.airbnb.paris.annotations.BeforeStyle
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.check
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

internal class BeforeStyleInfo private constructor(
        val enclosingElement: Element,
        val elementName: String) {

    companion object {

        fun fromEnvironment(roundEnv: RoundEnvironment): List<BeforeStyleInfo> {
            return roundEnv.getElementsAnnotatedWith(BeforeStyle::class.java)
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
        private fun fromElement(element: ExecutableElement): BeforeStyleInfo {
            check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
                "Methods annotated with @BeforeStyle can't be private or protected"
            }

            // TODO Check that the method takes a style as a parameter

            val enclosingElement = element.enclosingElement
            val elementName = element.simpleName.toString()

            return BeforeStyleInfo(enclosingElement, elementName)
        }
    }
}
