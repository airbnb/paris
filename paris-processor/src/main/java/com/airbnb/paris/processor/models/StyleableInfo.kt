package com.airbnb.paris.processor.models

import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.ParisProcessor
import com.squareup.javapoet.ClassName

internal class StyleableInfoExtractor(val processor: ParisProcessor)  {

    private val mutableModels = mutableListOf<StyleableInfo>()

    val models get() = mutableModels.toList()

    fun process(
        roundEnv: XRoundEnv,
        classesToStyleableChildInfo: Map<XTypeElement, List<StyleableChildInfo>>,
        classesToBeforeStyleInfo: Map<XTypeElement, List<BeforeStyleInfo>>,
        classesToAfterStyleInfo: Map<XTypeElement, List<AfterStyleInfo>>,
        classesToAttrsInfo: Map<XTypeElement, List<AttrInfo>>,
        classesToStylesInfo: Map<XTypeElement, List<StyleInfo>>
    ): List<StyleableInfo> {

        val styleableElements = roundEnv.getElementsAnnotatedWith(Styleable::class).filterIsInstance<XTypeElement>()

        val classesMissingStyleableAnnotation =
            (classesToStyleableChildInfo + classesToAttrsInfo + classesToStylesInfo)
                .filter { (clazz, _) -> clazz !in styleableElements }
                .keys

        classesMissingStyleableAnnotation.forEach {
            processor.logError(it) {
                "Uses @Attr, @StyleableChild and/or @Style but is not annotated with @Styleable."
            }
        }

        return styleableElements.mapNotNull {
            fromElement(
                element = it,
                styleableChildren = classesToStyleableChildInfo[it] ?: emptyList(),
                beforeStyles = classesToBeforeStyleInfo[it] ?: emptyList(),
                afterStyles = classesToAfterStyleInfo[it] ?: emptyList(),
                attrs = classesToAttrsInfo[it] ?: emptyList(),
                styles = classesToStylesInfo[it] ?: emptyList()
            )
        }.also {
            mutableModels.addAll(it)
        }
    }

    private fun fromElement(
        element: XTypeElement,
        styleableChildren: List<StyleableChildInfo>,
        beforeStyles: List<BeforeStyleInfo>,
        afterStyles: List<AfterStyleInfo>,
        attrs: List<AttrInfo>,
        styles: List<StyleInfo>
    ): StyleableInfo? {

        val baseStyleableInfo = BaseStyleableInfoExtractor(processor).fromElement(element)

        if (baseStyleableInfo.styleableResourceName.isEmpty() && (attrs.isNotEmpty() || styleableChildren.isNotEmpty())) {
            processor.logError(element) {
                "@Styleable is missing its value parameter (@Attr or @StyleableChild won't work otherwise)."
            }
            return null
        }

        if (baseStyleableInfo.styleableResourceName.isNotEmpty() && styleableChildren.isEmpty() && attrs.isEmpty()) {
            processor.logWarning(element) {
                "No need to specify the @Styleable value parameter if no class members are annotated with @Attr."
            }
        }

        return StyleableInfo(
            processor = processor,
            element = element,
            styleableChildren = styleableChildren,
            beforeStyles = beforeStyles,
            afterStyles = afterStyles,
            attrs = attrs,
            styles = styles,
            baseStyleableInfo = baseStyleableInfo
        )
    }
}

/**
 * If [styleableResourceName] isn't empty then at least one of [styleableChildren] or [attrs] won't be
 * empty either
 */
internal class StyleableInfo(
    val processor: ParisProcessor,
    val element: XTypeElement,
    val styleableChildren: List<StyleableChildInfo>,
    val beforeStyles: List<BeforeStyleInfo>,
    val afterStyles: List<AfterStyleInfo>,
    val attrs: List<AttrInfo>,
    val styles: List<StyleInfo>,
    baseStyleableInfo: BaseStyleableInfo
) : BaseStyleableInfo(baseStyleableInfo) {

    /**
     * A styleable declaration is guaranteed to be in the same R file as any attribute or styleable child.
     * `min` is used to ensure in the case there are multiple R files, a consistent one is chosen.
     */
    val styleableRClassName: ClassName? = (attrs.map { it.styleableResId.rClassName } + styleableChildren.map { it.styleableResId.rClassName }).minOrNull()

    /**
     * Applies lower camel case formatting
     */
    fun attrResourceNameToCamelCase(name: String): String {
        val prefix = "${styleableResourceName}_"
        if (!name.startsWith(prefix)) {
            processor.logError(element) {
                "Attribute \"$name\" does not belong to styleable declaration \"$styleableResourceName\"."
            }
        }

        val formattedName = name.removePrefix("${styleableResourceName}_").removePrefix("android_")
        return formattedName
            .foldRightIndexed("") { index, c, acc ->
                if (c == '_') {
                    acc
                } else {
                    if (index == 0 || formattedName[index - 1] != '_') {
                        c + acc
                    } else {
                        c.toUpperCase() + acc
                    }
                }
            }.decapitalize()
    }
}

