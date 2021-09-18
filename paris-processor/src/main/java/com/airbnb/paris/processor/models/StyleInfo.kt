package com.airbnb.paris.processor.models

import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import java.util.Locale

private const val DEFAULT_STYLE_FORMATTED_NAME = "Default"

internal class StyleInfoExtractor(val processor: ParisProcessor) {

    var models = emptyList<StyleInfo>()
        private set

    var latest = emptyList<StyleInfo>()
        private set

    private val styleCompanionPropertyInfoExtractor = StyleStaticPropertyInfoExtractor(processor)
    private val styleStaticMethodInfoExtractor = StyleStaticMethodInfoExtractor(processor)

    fun process(roundEnv: XRoundEnv) {
        // TODO Check that no style was left behind?

        val styleableElements = roundEnv.getElementsAnnotatedWith(Styleable::class).filterIsInstance<XTypeElement>()

        // TODO Make sure there aren't conflicting names?
        styleCompanionPropertyInfoExtractor.process(roundEnv)
        styleStaticMethodInfoExtractor.process(roundEnv)

        val stylesFromStyleAnnotation = (styleCompanionPropertyInfoExtractor.latest as List<StyleInfo>)
            .plus(styleStaticMethodInfoExtractor.latest)
            .groupBy { it.enclosingElement }

        styleableElements
            .map { it to (stylesFromStyleAnnotation[it] ?: emptyList()) }
            .flatMap<Pair<XTypeElement, List<StyleInfo>>, StyleInfo> { (styleableElement, styles) ->
                val styleWithNameDefault = styles.find { it.formattedName == DEFAULT_STYLE_FORMATTED_NAME }
                val styleMarkedAsDefault = styles.find { it.isDefault }

                if (styleWithNameDefault != styleMarkedAsDefault && styleWithNameDefault != null && styleMarkedAsDefault != null) {
                    processor.logError(styleableElement) {
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
                        is StyleStaticPropertyInfo -> StyleStaticPropertyInfo(
                            env = processor.environment,
                            element = styleMarkedAsDefault.element,
                            elementName = styleMarkedAsDefault.elementName,
                            formattedName = DEFAULT_STYLE_FORMATTED_NAME,
                            javadoc = styleMarkedAsDefault.javadoc,
                            kdoc = styleMarkedAsDefault.kdoc,
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
                        else -> error("Unsupported $styleMarkedAsDefault")
                    }
                } else {
                    // Next we check to see if a style exists that matches the default name
                    // format, otherwise we add an empty, no-op style as a default
                    val defaultNameFormatStyle = fromDefaultNameFormat(styleableElement)
                    if (defaultNameFormatStyle != null) {
                        styles + defaultNameFormatStyle
                    } else {
                        if (processor.namespacedResourcesEnabled && !styleableElement.getAnnotation(Styleable::class)!!.value.emptyDefaultStyle) {
                            processor.logError(styleableElement) {
                                "No default style found for ${styleableElement.name}. Link an appropriate default style, " +
                                        "or set @Styleable(emptyDefaultStyle = true) for this element if none exist."
                            }
                        }
                        styles + EmptyStyleInfo(styleableElement, true)
                    }
                }
            }
            .let {
                models += it
                latest = it
            }
    }

    private fun fromDefaultNameFormat(styleableElement: XTypeElement): StyleInfo? {
        if (processor.defaultStyleNameFormat.isBlank()) {
            return null
        }

        val elementName = styleableElement.name
        val defaultStyleName = String.format(Locale.US, processor.defaultStyleNameFormat, elementName)

        val rStyleTypeElement = processor.memoizer.rStyleTypeElementX
        val defaultStyleExists = rStyleTypeElement != null && rStyleTypeElement.getDeclaredFields().any {
            it.name == defaultStyleName
        }

        if (defaultStyleExists) {
            val styleResourceCode = JavaCodeBlock.of("\$T.\$L", rStyleTypeElement?.className, defaultStyleName)

            val javadoc = JavaCodeBlock.of("See $defaultStyleName style (defined as an XML resource).")
            val kdoc = KotlinCodeBlock.of(javadoc.toString())

            return StyleResInfo(
                styleableElement,
                defaultStyleName,
                DEFAULT_STYLE_FORMATTED_NAME,
                javadoc,
                kdoc,
                isDefault = true,
                styleResourceCode = styleResourceCode
            )
        } else {
            return null
        }
    }
}

internal interface StyleInfo {
    val enclosingElement: XTypeElement
    val elementName: String
    val formattedName: String

    //    val styleResourceCode: JavaCodeBlock?
    val javadoc: JavaCodeBlock
    val kdoc: KotlinCodeBlock
    val isDefault: Boolean
}

class EmptyStyleInfo(override val enclosingElement: XTypeElement, override val isDefault: Boolean) : StyleInfo {

    override val elementName = "empty_default"
    override val formattedName = DEFAULT_STYLE_FORMATTED_NAME
    override val javadoc: JavaCodeBlock = JavaCodeBlock.of("Empty style.")
    override val kdoc = KotlinCodeBlock.of(javadoc.toString())
}

class StyleResInfo(
    override val enclosingElement: XTypeElement,
    override val elementName: String,
    override val formattedName: String,
    override val javadoc: JavaCodeBlock,
    override val kdoc: KotlinCodeBlock,
    override val isDefault: Boolean,
    val styleResourceCode: JavaCodeBlock
) : StyleInfo
