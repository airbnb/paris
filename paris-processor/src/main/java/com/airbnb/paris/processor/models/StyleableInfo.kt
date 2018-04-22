package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.framework.errors.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import kotlin.also

internal class StyleableInfoExtractor {

    private val mutableModels = mutableListOf<StyleableInfo>()

    val models get() = mutableModels.toList()

    fun process(
            roundEnv: RoundEnvironment,
            classesToStyleableChildInfo: Map<TypeElement, List<StyleableChildInfo>>,
            classesToBeforeStyleInfo: Map<TypeElement, List<BeforeStyleInfo>>,
            classesToAfterStyleInfo: Map<TypeElement, List<AfterStyleInfo>>,
            classesToAttrsInfo: Map<TypeElement, List<AttrInfo>>,
            classesToStylesInfo: Map<Element, List<StyleInfo>>
    ): List<StyleableInfo> {
        val styleableElements = roundEnv.getElementsAnnotatedWith(Styleable::class.java)

        val classesMissingStyleableAnnotation = (classesToStyleableChildInfo + classesToAttrsInfo + classesToStylesInfo)
                .filter { (`class`, _) -> `class` !in styleableElements }
                .keys
        check(classesMissingStyleableAnnotation.isEmpty()) {
            "The class \"${classesMissingStyleableAnnotation.first().simpleName}\" uses @Attr, @StyleableChild and/or @Style but is not annotated with @Styleable"
        }

        return styleableElements
                .mapNotNull {
                    try {
                        fromElement(it as TypeElement,
                                classesToStyleableChildInfo[it] ?: emptyList(),
                                classesToBeforeStyleInfo[it] ?: emptyList(),
                                classesToAfterStyleInfo[it] ?: emptyList(),
                                classesToAttrsInfo[it] ?: emptyList(),
                                classesToStylesInfo[it] ?: emptyList())
                    } catch (e: ProcessorException) {
                        Errors.log(e)
                        null
                    }
                }
                .also {
                    mutableModels.addAll(it)
                }
    }

    @Throws(ProcessorException::class)
    private fun fromElement(
            element: TypeElement,
            styleableChildren: List<StyleableChildInfo>,
            beforeStyles: List<BeforeStyleInfo>,
            afterStyles: List<AfterStyleInfo>,
            attrs: List<AttrInfo>,
            styles: List<StyleInfo>
    ): StyleableInfo {

        val lightStyleableInfo = BaseStyleableInfoExtractor().fromElement(element)

        check(lightStyleableInfo.styleableResourceName.isNotEmpty() || (attrs.isEmpty() && styleableChildren.isEmpty())) {
            "@Styleable is missing its value parameter (@Attr or @StyleableChild won't work otherwise)"
        }
        check(lightStyleableInfo.styleableResourceName.isEmpty() || !(styleableChildren.isEmpty() && attrs.isEmpty())) {
            "No need to specify the @Styleable value parameter if no class members are annotated with @Attr"
        }

        return StyleableInfo(
                styleableChildren,
                beforeStyles,
                afterStyles,
                attrs,
                styles,
                lightStyleableInfo
        )
    }
}

/**
 * If [styleableResourceName] isn't empty then at least one of [styleableChildren] or [attrs] won't be
 * empty either
 */
internal class StyleableInfo(
        val styleableChildren: List<StyleableChildInfo>,
        val beforeStyles: List<BeforeStyleInfo>,
        val afterStyles: List<AfterStyleInfo>,
        val attrs: List<AttrInfo>,
        val styles: List<StyleInfo>,
        baseStyleableInfo: BaseStyleableInfo
) : BaseStyleableInfo(baseStyleableInfo) {

    /**
     * Applies lower camel case formatting
     */
    fun attrResourceNameToCamelCase(name: String): String {
        var formattedName = name.removePrefix("${styleableResourceName}_")
        formattedName = formattedName.removePrefix("android_")
        formattedName = formattedName.foldRightIndexed("") { index, c, acc ->
            if (c == '_') {
                acc
            } else {
                if (index == 0 || formattedName[index - 1] != '_') {
                    c + acc
                } else {
                    c.toUpperCase() + acc
                }
            }
        }
        formattedName = formattedName.first().toLowerCase() + formattedName.drop(1)
        return formattedName
    }
}

