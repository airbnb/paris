package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.WithParisProcessor
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

private const val DEFAULT_STYLE_FORMATTED_NAME = "Default"

internal class StyleInfoExtractor(override val processor: ParisProcessor) : WithParisProcessor {

    var models = emptyList<StyleInfo>()
        private set

    var latest = emptyList<StyleInfo>()
        private set

    private var styleCompanionPropertyInfoExtractor = StyleCompanionPropertyInfoExtractor(processor)
    private var styleStaticMethodInfoExtractor = StyleStaticMethodInfoExtractor(processor)

    fun process(roundEnv: RoundEnvironment) {
        // TODO Check that no style was left behind?

        val styleableElements = roundEnv.getElementsAnnotatedWith(Styleable::class.java)

        // TODO Make sure there aren't conflicting names?
        styleCompanionPropertyInfoExtractor.process(roundEnv)
        styleStaticMethodInfoExtractor.process(roundEnv)

        val stylesFromStyleAnnotation = (styleCompanionPropertyInfoExtractor.latest as List<StyleInfo>)
            .plus(styleStaticMethodInfoExtractor.latest)
            .groupBy { it.enclosingElement }

        styleableElements
            .map { it to (stylesFromStyleAnnotation[it] ?: emptyList()) }
            .flatMap<Pair<Element, List<StyleInfo>>, StyleInfo> { (styleableElement, styles) ->
                val styleWithNameDefault = styles.find { it.formattedName == DEFAULT_STYLE_FORMATTED_NAME }
                val styleMarkedAsDefault = styles.find { it.isDefault }

                if (styleWithNameDefault != styleMarkedAsDefault && styleWithNameDefault != null && styleMarkedAsDefault != null) {
                    logError(styleableElement) {
                        "Naming a linked style \"default\" and annotating another with @Style(isDefault = true) is invalid."
                    }
                }

                if (styleWithNameDefault != null) {
                    // Great! There's already a style named "default"
                    styles
                } else if (styleMarkedAsDefault != null) {
                    // One style is marked as being the default so let's duplicate it with the name "default"

                    // We suppress this warning because it is wrong, not casting results in an error
                    @Suppress("USELESS_CAST")
                    styles + when (styleMarkedAsDefault) {
                        is StyleCompanionPropertyInfo -> StyleCompanionPropertyInfo(
                            styleMarkedAsDefault.element,
                            styleMarkedAsDefault.elementName,
                            DEFAULT_STYLE_FORMATTED_NAME,
                            styleMarkedAsDefault.javadoc,
                            styleMarkedAsDefault.kdoc,
                            isDefault = true
                        ) as StyleInfo
                        is StyleStaticMethodInfo -> StyleStaticMethodInfo(
                            styleMarkedAsDefault.element,
                            styleMarkedAsDefault.elementName,
                            DEFAULT_STYLE_FORMATTED_NAME,
                            styleMarkedAsDefault.javadoc,
                            styleMarkedAsDefault.kdoc,
                            isDefault = true
                        ) as StyleInfo
                        else -> throw IllegalStateException()
                    }
                } else {
                    // Next we check to see if a style exists that matches the default name
                    // format, otherwise we add an empty, no-op style as a default
                    val defaultNameFormatStyle = fromDefaultNameFormat(styleableElement)
                    if (defaultNameFormatStyle != null) {
                        styles + defaultNameFormatStyle
                    } else {
                        styles + EmptyStyleInfo(styleableElement, true)
                    }
                }
            }
            .let {
                models += it
                latest = it
            }
    }

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

            return StyleResInfo(
                styleableElement,
                defaultStyleName,
                DEFAULT_STYLE_FORMATTED_NAME,
                javadoc,
                kdoc,
                true,
                styleResourceCode
            )
        } else {
            return null
        }
    }
}

internal interface StyleInfo {
    val enclosingElement: Element
    val elementName: String
    val formattedName: String
    //    val styleResourceCode: JavaCodeBlock?
    val javadoc: JavaCodeBlock
    val kdoc: KotlinCodeBlock
    val isDefault: Boolean
}

class EmptyStyleInfo(override val enclosingElement: Element, override val isDefault: Boolean) : StyleInfo {

    override val elementName = "empty_default"
    override val formattedName = DEFAULT_STYLE_FORMATTED_NAME
    override val javadoc: JavaCodeBlock = JavaCodeBlock.of("Empty style")
    override val kdoc = KotlinCodeBlock.of(javadoc.toString())
}

class StyleResInfo(
    override val enclosingElement: Element,
    override val elementName: String,
    override val formattedName: String,
    override val javadoc: JavaCodeBlock,
    override val kdoc: KotlinCodeBlock,
    override val isDefault: Boolean,
    val styleResourceCode: JavaCodeBlock
) : StyleInfo
