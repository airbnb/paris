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

internal data class StyleInfo constructor(
        val elementKind: Kind,
        val enclosingElement: Element,
        val elementName: String,
        val formattedName: String,
        val styleResourceCode: CodeBlock?,
        private val isDefault: Boolean = false) {

    enum class Kind {
        FIELD, METHOD, STYLE_RES, EMPTY
    }

    companion object {

        lateinit var processor: ParisProcessor
        lateinit var elementUtils: Elements
        lateinit var typeUtils: Types

        fun fromEnvironment(processor: ParisProcessor, roundEnv: RoundEnvironment): List<StyleInfo> {
            this.processor = processor
            this.elementUtils = processor.elementUtils
            this.typeUtils = processor.typeUtils

            val styleableElements = roundEnv.getElementsAnnotatedWith(Styleable::class.java)

            // TODO Make sure there aren't conflicting names?
            val stylesFromStyleAnnotation = roundEnv.getElementsAnnotatedWith(Style::class.java)
                    .mapNotNull {
                        try {
                            fromStyleElement(it)
                        } catch (e: ProcessorException) {
                            Errors.log(e)
                            null
                        }
                    }
                    .groupBy { it.enclosingElement }

            // TODO Check that no style was left behind?

            return styleableElements
                    .map { it to (stylesFromStyleAnnotation[it] ?: emptyList()) }
                    .flatMap { (styleableElement, styles) ->
                        val styleWithNameDefault = styles.find { it.formattedName == "Default" }
                        val styleMarkedAsDefault = styles.find { it.isDefault }

                        check(styleWithNameDefault == styleMarkedAsDefault
                                || styleWithNameDefault == null
                                || styleMarkedAsDefault == null) {
                            "Naming a linked style \"default\" and annotating another with @Style(isDefault = true) is invalid"
                        }

                        if (styleWithNameDefault != null) {
                            // Great! There's already a style named "default"
                            styles
                        } else if (styleMarkedAsDefault != null) {
                            // One style is marked as being the default so let's duplicate it with the name "default"
                            styles + styleMarkedAsDefault.copy(formattedName = "Default")
                        } else {
                            // Next we check to see if a style exists that matches the default name
                            // format, otherwise we add an empty, no-op style as a default
                            val defaultNameFormatStyle = fromDefaultNameFormat(styleableElement)
                            if (defaultNameFormatStyle != null) {
                                styles + defaultNameFormatStyle
                            } else {
                                styles + emptyDefaultFromStyleableElement(styleableElement)
                            }
                        }
                    }
        }

        @Throws(ProcessorException::class)
        private fun emptyDefaultFromStyleableElement(styleableElement: Element): StyleInfo {
            val elementName = styleableElement.simpleName.toString()

            return StyleInfo(
                    Kind.EMPTY,
                    styleableElement,
                    "empty_default",
                    "Default",
                    null
            )
        }

        @Throws(ProcessorException::class)
        private fun fromDefaultNameFormat(styleableElement: Element): StyleInfo? {
            if (processor.defaultStyleNameFormat.isBlank()) {
                return null
            }

            check(processor.rType != null)

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

            val style = element.getAnnotation(Style::class.java)
            val isDefault = style.isDefault

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
                    null, // No style resource
                    isDefault
            )
        }
    }
}
