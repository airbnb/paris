package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.asTypeElement
import com.airbnb.paris.processor.utils.check
import com.squareup.javapoet.CodeBlock
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class StyleInfo private constructor(
        val elementKind: Kind,
        val enclosingElement: Element,
        val elementName: String,
        val formattedName: String,
        val styleResourceCode: CodeBlock?) {

    enum class Kind {
        FIELD, METHOD, STYLE_RES
    }

    companion object {

        lateinit var processor: ParisProcessor
        lateinit var elementUtils: Elements
        lateinit var typeUtils: Types

        fun fromEnvironment(processor: ParisProcessor, roundEnv: RoundEnvironment): List<StyleInfo> {
            this.processor = processor
            this.elementUtils = processor.elementUtils
            this.typeUtils = processor.typeUtils

            val stylesFromDefaultNameFormat: List<StyleInfo>
            if (processor.defaultStyleNameFormat.isBlank()) {
                stylesFromDefaultNameFormat = emptyList()
            } else {
                stylesFromDefaultNameFormat = roundEnv.getElementsAnnotatedWith(Styleable::class.java)
                        .mapNotNull {
                            try {
                                fromDefaultNameFormat(it)
                            } catch (e: ProcessorException) {
                                Errors.log(e)
                                null
                            }
                        }
            }

            val stylesFromStyleAnnotation = roundEnv.getElementsAnnotatedWith(Style::class.java)
                    .mapNotNull {
                        try {
                            fromStyleElement(it)
                        } catch (e: ProcessorException) {
                            Errors.log(e)
                            null
                        }
                    }

            return stylesFromDefaultNameFormat.plus(stylesFromStyleAnnotation)
        }

        @Throws(ProcessorException::class)
        private fun fromDefaultNameFormat(styleableElement: Element): StyleInfo? {
            check(processor.rType != null)
            check(processor.defaultStyleNameFormat.isNotBlank())

            val elementName = styleableElement.simpleName.toString()
            val defaultStyleName = String.format(Locale.US, processor.defaultStyleNameFormat, elementName)

            val rStyleTypeElement = elementUtils.getTypeElement("${processor.rType!!.asTypeElement(typeUtils).qualifiedName}.style")
            val defaultStyleExists = elementUtils.getAllMembers(rStyleTypeElement).any {
                it.simpleName.toString() == defaultStyleName
            }

            if (defaultStyleExists) {
                val styleResourceCode = CodeBlock.of("\$T.\$L", rStyleTypeElement, defaultStyleName)

                return StyleInfo(
                        Kind.STYLE_RES,
                        styleableElement,
                        defaultStyleName,
                        "Default",
                        styleResourceCode
                )
            } else {
                return null
            }
        }

        @Throws(ProcessorException::class)
        private fun fromStyleElement(element: Element): StyleInfo {
            // TODO Get Javadoc from field/method and add it to the generated methods

            check(element.modifiers.contains(Modifier.STATIC)
                    && !element.modifiers.contains(Modifier.PRIVATE)
                    && !element.modifiers.contains(Modifier.PROTECTED), element) {
                "Fields and methods annotated with @Style must be static and can't be private or protected"
            }

            val enclosingElement = element.enclosingElement

            val elementName = element.simpleName.toString()

            // Converts any name to CamelCase
            val isNameAllCaps = elementName.all { it.isUpperCase() || !it.isLetter() }
            val formattedName = elementName.foldRightIndexed("") { index, c, acc ->
                if (c == '_') {
                    acc
                } else {
                    if (index == 0) {
                        c.toUpperCase() + acc
                    } else if (elementName[index - 1] != '_') {
                        if (isNameAllCaps) {
                            c.toLowerCase() + acc
                        } else {
                            c + acc
                        }
                    } else {
                        c.toUpperCase() + acc
                    }
                }
            }

            check(element.kind == ElementKind.FIELD || element.kind == ElementKind.METHOD) {
                "@Style can only be used on fields and methods"
            }

            val elementKind: Kind
            val targetType: TypeMirror
            if (element.kind == ElementKind.FIELD) {
                check(element.modifiers.contains(Modifier.FINAL), element) {
                    "Fields annotated with @Style must be final"
                }

                elementKind = Kind.FIELD
                // TODO Check that the target type is an int
                targetType = element.asType()

            } else { // Method
                elementKind = Kind.METHOD
                // TODO Check that the target type is a builder
                targetType = (element as ExecutableElement).parameters[0].asType()

            }

            return StyleInfo(
                    elementKind,
                    enclosingElement,
                    elementName,
                    formattedName,
                    null // No style resource
            )
        }
    }
}
