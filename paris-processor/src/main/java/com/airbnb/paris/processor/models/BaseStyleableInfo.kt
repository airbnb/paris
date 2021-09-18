package com.airbnb.paris.processor.models

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.annotations.GeneratedStyleableClass
import com.airbnb.paris.annotations.GeneratedStyleableModule
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.PARIS_MODULES_PACKAGE_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT
import com.airbnb.paris.processor.utils.getTypeElementsFromPackageSafe
import com.squareup.javapoet.ClassName

/**
 * It's important that base styleables be extracted before new ones are written for the current module, otherwise the latter will be included in the
 * results
 */
internal class BaseStyleableInfoExtractor( val processor: ParisProcessor) {

    fun fromEnvironment(): List<BaseStyleableInfo> {
        return processor.environment.getTypeElementsFromPackageSafe(PARIS_MODULES_PACKAGE_NAME)
            .mapNotNull { it.getAnnotation(GeneratedStyleableModule::class) }
            .flatMap { styleableModule ->
                styleableModule.getAsAnnotationBoxArray<GeneratedStyleableClass>("value")
                    .mapNotNull { it.getAsType("value")?.typeElement }
                    .map { BaseStyleableInfoExtractor(processor).fromElement(it) }
            }
    }

    fun fromElement(element: XTypeElement): BaseStyleableInfo {
        val elementPackageName = element.packageName
        val elementName = element.name
        val elementType = element.type

        val viewElementType: XType = if (processor.memoizer.proxyClassType.rawType.isAssignableFrom(elementType)) {
            // Get the parameterized type, which should be the view type
            element.superType?.typeArguments?.getOrNull(1) ?: error("No type for $elementType")
        } else {
            elementType
        }

        val viewElement = viewElementType.typeElement!!
        val viewElementPackageName = viewElement.packageName
        val viewElementName = viewElement.name

        val styleable = element.getAnnotation(Styleable::class)?.value!!
        val styleableResourceName = styleable.value

        return BaseStyleableInfo(
            annotatedElement = element,
            elementPackageName = elementPackageName,
            elementName = elementName,
            elementType = elementType,
            viewElementPackageName = viewElementPackageName,
            viewElement = viewElement,
            viewElementName = viewElementName,
            viewElementType = viewElementType,
            styleableResourceName = styleableResourceName
        )
    }
}

internal open class BaseStyleableInfo(
    /**
     * The element that is annotated with @Styleable.
     * This is used to determine the originating element of generated files.
     */
    val annotatedElement: XElement,
    val elementPackageName: String,
    val elementName: String,
    /**
     * If the styleable class is not a proxy, will be equal to [viewElementType]. Otherwise,
     * will refer to the proxy class
     */
    val elementType: XType,
    private val viewElementPackageName: String,
    val viewElement: XTypeElement,
    /** The simple name of the view eg. "AirImageView" */
    val viewElementName: String,
    /**
     * If the styleable class is not a proxy, will be equal to [elementType]. Refers to the view
     * class
     */
    val viewElementType: XType,
    val styleableResourceName: String
) {

    constructor(baseStyleableInfo: BaseStyleableInfo) : this(
        annotatedElement = baseStyleableInfo.annotatedElement,
        elementPackageName = baseStyleableInfo.elementPackageName,
        elementName = baseStyleableInfo.elementName,
        elementType = baseStyleableInfo.elementType,
        viewElementPackageName = baseStyleableInfo.viewElementPackageName,
        viewElement = baseStyleableInfo.viewElement,
        viewElementName = baseStyleableInfo.viewElementName,
        viewElementType = baseStyleableInfo.viewElementType,
        styleableResourceName = baseStyleableInfo.styleableResourceName
    )

    val styleApplierClassName: ClassName = ClassName.get(
        viewElementPackageName,
        String.format(STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT, viewElementName)
    )

    val styleBuilderClassName: ClassName = styleApplierClassName.nestedClass("StyleBuilder")
}

