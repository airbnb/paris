package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.GeneratedStyleableModule
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.PARIS_MODULES_PACKAGE_NAME
import com.airbnb.paris.processor.PROXY_CLASS_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT
import com.airbnb.paris.processor.framework.WithSkyProcessor
import com.airbnb.paris.processor.framework.packageName
import com.squareup.javapoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

/**
 * It's important that base styleables be extracted before new ones are written for the current module, otherwise the latter will be included in the
 * results
 */
internal class BaseStyleableInfoExtractor(override val processor: ParisProcessor) : WithSkyProcessor {

    fun fromEnvironment(): List<BaseStyleableInfo> {
        val baseStyleablesInfo = mutableListOf<BaseStyleableInfo>()
        elements.getPackageElement(PARIS_MODULES_PACKAGE_NAME)?.let { packageElement ->
            packageElement.enclosedElements
                .map { it.getAnnotation(GeneratedStyleableModule::class.java) }
                .forEach { styleableModule ->
                    baseStyleablesInfo.addAll(
                        styleableModule.value
                            .mapNotNull { generatedStyleableClass ->
                                var typeElement: TypeElement? = null
                                try {
                                    generatedStyleableClass.value
                                } catch (e: MirroredTypeException) {
                                    typeElement = e.typeMirror.asTypeElement()
                                }
                                typeElement
                            }
                            .map { typeElement ->
                                BaseStyleableInfoExtractor(processor).fromElement(typeElement)
                            }
                    )
                }
        }
        return baseStyleablesInfo
    }

    fun fromElement(element: TypeElement): BaseStyleableInfo {
        val elementPackageName = element.packageName
        val elementName = element.simpleName.toString()
        val elementType = element.asType()

        val viewElementType: TypeMirror
        viewElementType = if (isSubtype(elementType, processor.memoizer.proxyClassTypeErased)) {
            // Get the parameterized type, which should be the view type
            (element.superclass as DeclaredType).typeArguments[1]
        } else {
            elementType
        }

        val viewElement = viewElementType.asTypeElement()
        val viewElementPackageName = viewElement.packageName
        val viewElementName = viewElement.simpleName.toString()

        val styleable = element.getAnnotation(Styleable::class.java)
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
    val annotatedElement: Element,
    val elementPackageName: String,
    val elementName: String,
    /**
     * If the styleable class is not a proxy, will be equal to [viewElementType]. Otherwise,
     * will refer to the proxy class
     */
    val elementType: TypeMirror,
    private val viewElementPackageName: String,
    val viewElement: TypeElement,
    /** The simple name of the view eg. "AirImageView" */
    val viewElementName: String,
    /**
     * If the styleable class is not a proxy, will be equal to [elementType]. Refers to the view
     * class
     */
    val viewElementType: TypeMirror,
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

