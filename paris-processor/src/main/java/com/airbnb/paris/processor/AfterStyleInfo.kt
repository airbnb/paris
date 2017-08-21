package com.airbnb.paris.processor

import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.check
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

internal class AfterStyleInfo private constructor(
        val enclosingElement: Element,
        val elementName: String) {

    companion object {

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

            // TODO Check that the method takes a style as a parameter

            val enclosingElement = element.enclosingElement
            val elementName = element.simpleName.toString()

            return AfterStyleInfo(enclosingElement, elementName)
        }
    }
}
