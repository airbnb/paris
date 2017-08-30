package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.ClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * If [styleableResourceName] isn't empty then at least one of [styleableFields] or [attrs] won't be
 * empty either
 */
internal class StyleableInfo private constructor(
        val styleableFields: List<StyleableFieldInfo>,
        val beforeStyles: List<BeforeStyleInfo>,
        val afterStyles: List<AfterStyleInfo>,
        val attrs: List<AttrInfo>,
        val elementPackageName: String,
        val elementName: String,
        /**
         * If the styleable class is not a proxy, will be equal to [viewElementType]. Otherwise,
         * will refer to the proxy class
         */
        val elementType: TypeMirror,
        /**
         * If the styleable class is not a proxy, will be equal to [elementType]. Refers to the view
         * class
         */
        val viewElementType: TypeMirror,
        val styleableResourceName: String,
        val styles: List<StyleInfo>) {

    fun styleApplierClassName(): ClassName =
            ClassName.get(elementPackageName, String.format(ParisProcessor.STYLE_APPLIER_CLASS_NAME_FORMAT, elementName))

    companion object {

        fun fromEnvironment(roundEnv: RoundEnvironment, elementUtils: Elements, typeUtils: Types,
                            classesToStyleableFieldInfo: Map<Element, List<StyleableFieldInfo>>,
                            classesToBeforeStyleInfo: Map<Element, List<BeforeStyleInfo>>,
                            classesToAfterStyleInfo: Map<Element, List<AfterStyleInfo>>,
                            classesToAttrsInfo: Map<Element, List<AttrInfo>>,
                            classesToStylesInfo: Map<Element, List<StyleInfo>>): List<StyleableInfo> {
            val styleableElements = roundEnv.getElementsAnnotatedWith(Styleable::class.java)

            val classesMissingStyleableAnnotation = (classesToStyleableFieldInfo + classesToAttrsInfo + classesToStylesInfo)
                    .filter { (`class`, _) -> `class` !in styleableElements }
                    .keys
            check(classesMissingStyleableAnnotation.isEmpty()) {
                "The class \"${classesMissingStyleableAnnotation.first().simpleName}\" uses @Attr, @StyleableField and/or @Style but is not annotated with @Styleable"
            }

            return styleableElements
                    .mapNotNull {
                        try {
                            fromElement(elementUtils, typeUtils, it as TypeElement,
                                    classesToStyleableFieldInfo[it] ?: emptyList(),
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
                                styleableFields: List<StyleableFieldInfo>,
                                beforeStyles: List<BeforeStyleInfo>,
                                afterStyles: List<AfterStyleInfo>,
                                attrs: List<AttrInfo>,
                                styles: List<StyleInfo>): StyleableInfo {

            val elementPackageName = element.packageName
            val elementName = element.simpleName.toString()
            val elementType = element.asType()

            val viewElementType: TypeMirror
            if (typeUtils.isSubtype(elementType, typeUtils.erasure(elementUtils.PROXY_TYPE.asType()))) {
                // Get the parameterized type, which should be the view type
                viewElementType = (element.superclass as DeclaredType).typeArguments[1]
            } else {
                viewElementType = elementType
            }

            val styleable = element.getAnnotation(Styleable::class.java)
            val styleableResourceName = styleable.value

            check(styleableResourceName.isNotEmpty() || (attrs.isEmpty() && styleableFields.isEmpty())) {
                "@Styleable is missing its value parameter (@Attr or @StyleableField won't work otherwise)"
            }
            check(styleableResourceName.isEmpty() || !(styleableFields.isEmpty() && attrs.isEmpty())) {
                "No need to specify the @Styleable value parameter if no class members are annotated with @Attr"
            }

            return StyleableInfo(
                    styleableFields,
                    beforeStyles,
                    afterStyles,
                    attrs,
                    elementPackageName,
                    elementName,
                    elementType,
                    viewElementType,
                    styleableResourceName,
                    styles)
        }
    }
}
