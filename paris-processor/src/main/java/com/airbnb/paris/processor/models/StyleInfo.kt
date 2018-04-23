package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.RElement
import com.airbnb.paris.processor.defaultStyleNameFormat
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.elements
import com.airbnb.paris.processor.framework.errors.Errors
import com.airbnb.paris.processor.framework.errors.ProcessorException
import com.airbnb.paris.processor.framework.errors.check
import com.airbnb.paris.processor.models.StyleInfo.Kind
import com.airbnb.paris.processor.models.StyleInfo.Kind.*
import com.airbnb.paris.processor.utils.ParisProcessorUtils
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

internal class StyleInfoExtractor {

    var models = emptyList<StyleInfo>()
        private set

    var latest = emptyList<StyleInfo>()
        private set

    fun process(roundEnv: RoundEnvironment) {
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

        styleableElements
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
                .let {
                    models += it
                    latest = it
                }
    }

    @Throws(ProcessorException::class)
    private fun emptyDefaultFromStyleableElement(styleableElement: Element): StyleInfo {
        val javadoc = JavaCodeBlock.of("Empty style")
        val kdoc = KotlinCodeBlock.of(javadoc.toString())
        return StyleInfo(
                EMPTY,
                styleableElement,
                "empty_default",
                "Default",
                null,
                javadoc,
                kdoc
        )
    }

    @Throws(ProcessorException::class)
    private fun fromDefaultNameFormat(styleableElement: Element): StyleInfo? {
        if (defaultStyleNameFormat.isBlank()) {
            return null
        }

        val elementName = styleableElement.simpleName.toString()
        val defaultStyleName = String.format(Locale.US, defaultStyleNameFormat, elementName)

        val rStyleTypeElement = elements.getTypeElement("${RElement!!.qualifiedName}.style")
        val defaultStyleExists = elements.getAllMembers(rStyleTypeElement).any {
            it.simpleName.toString() == defaultStyleName
        }

        if (defaultStyleExists) {
            val styleResourceCode = JavaCodeBlock.of("\$T.\$L", rStyleTypeElement, defaultStyleName)

            val javadoc = JavaCodeBlock.of("See $defaultStyleName style (defined as an XML resource)")
            val kdoc = KotlinCodeBlock.of(javadoc.toString())

            return StyleInfo(
                    STYLE_RES,
                    styleableElement,
                    defaultStyleName,
                    "Default",
                    styleResourceCode,
                    javadoc,
                    kdoc
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

        val formattedName = ParisProcessorUtils.reformatStyleFieldOrMethodName(elementName)

        check(element.kind == ElementKind.FIELD || element.kind == ElementKind.METHOD) {
            "@Style can only be used on fields and methods"
        }

        val elementKind: Kind
        val targetType: TypeMirror
        val javadoc: JavaCodeBlock
        val kdoc: KotlinCodeBlock
        if (element.kind == ElementKind.FIELD) {
            check(element.modifiers.contains(Modifier.FINAL), element) {
                "Fields annotated with @Style must be final"
            }

            elementKind = FIELD
            // TODO Check that the target type is an int
            //targetType = element.asType()

            javadoc = JavaCodeBlock.of("@see \$T#\$N", enclosingElement, elementName)
            kdoc = KotlinCodeBlock.of("@see %T.%N", enclosingElement, elementName)

        } else { // Method
            elementKind = METHOD
            // TODO Check that the target type is a builder
            targetType = (element as ExecutableElement).parameters[0].asType()

            javadoc = JavaCodeBlock.of("@see \$T#\$N(\$T)", enclosingElement, elementName, targetType)
            kdoc = KotlinCodeBlock.of("@see %T.%N", enclosingElement, elementName)
        }

        return StyleInfo(
                elementKind,
                enclosingElement,
                elementName,
                formattedName,
                null, // No style resource
                javadoc,
                kdoc,
                isDefault
        )
    }
}

internal data class StyleInfo(
        val elementKind: Kind,
        val enclosingElement: Element,
        val elementName: String,
        val formattedName: String,
        val styleResourceCode: JavaCodeBlock?,
        val javadoc: JavaCodeBlock,
        val kdoc: KotlinCodeBlock,
        val isDefault: Boolean = false) {

    enum class Kind {
        FIELD, METHOD, STYLE_RES, EMPTY
    }
}
