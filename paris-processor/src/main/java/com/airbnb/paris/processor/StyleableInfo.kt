package com.airbnb.paris.processor

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.util.*

/**
 * If [styleableResourceName] isn't empty then at least one of [styleableChildren] or [attrs] won't be
 * empty either
 */
internal class StyleableInfo private constructor(
        val styleableChildren: List<StyleableChildInfo>,
        val beforeStyles: List<BeforeStyleInfo>,
        val afterStyles: List<AfterStyleInfo>,
        val attrs: List<AttrInfo>,
        val styles: List<StyleInfo>,
        baseStyleableInfo: BaseStyleableInfo
) : BaseStyleableInfo(
        baseStyleableInfo.elementPackageName,
        baseStyleableInfo.elementName,
        baseStyleableInfo.elementType,
        baseStyleableInfo.viewElementType,
        baseStyleableInfo.styleableResourceName
) {

    fun styleApplierClassName(): ClassName =
            ClassName.get(elementPackageName, String.format(STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT, elementName))

    companion object {

        fun fromEnvironment(roundEnv: RoundEnvironment, elementUtils: Elements, typeUtils: Types,
                            classesToStyleableChildInfo: Map<Element, List<StyleableChildInfo>>,
                            classesToBeforeStyleInfo: Map<Element, List<BeforeStyleInfo>>,
                            classesToAfterStyleInfo: Map<Element, List<AfterStyleInfo>>,
                            classesToAttrsInfo: Map<Element, List<AttrInfo>>,
                            classesToStylesInfo: Map<Element, List<StyleInfo>>): List<StyleableInfo> {
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
                            fromElement(elementUtils, typeUtils, it as TypeElement,
                                    classesToStyleableChildInfo[it] ?: emptyList(),
                                    classesToBeforeStyleInfo[it] ?: emptyList(),
                                    classesToAfterStyleInfo[it] ?: emptyList(),
                                    classesToAttrsInfo[it] ?: emptyList(),
                                    classesToStylesInfo[it] ?: emptyList())
                        } catch(e: ProcessorException) {
                            Errors.log(e)
                            null
                        }
                    }
        }

        @Throws(ProcessorException::class)
        private fun fromElement(elementUtils: Elements,
                                typeUtils: Types,
                                element: TypeElement,
                                styleableChildren: List<StyleableChildInfo>,
                                beforeStyles: List<BeforeStyleInfo>,
                                afterStyles: List<AfterStyleInfo>,
                                attrs: List<AttrInfo>,
                                styles: List<StyleInfo>): StyleableInfo {

            val lightStyleableInfo = BaseStyleableInfo.fromElement(elementUtils, typeUtils, element)

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
}
