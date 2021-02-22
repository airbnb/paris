package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.GeneratedStyleableModule
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.PARIS_MODULES_PACKAGE_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT
import com.airbnb.paris.processor.abstractions.XElement
import com.airbnb.paris.processor.abstractions.XType
import com.airbnb.paris.processor.abstractions.XTypeElement
import com.airbnb.paris.processor.abstractions.javac.JavacProcessingEnv
import com.airbnb.paris.processor.abstractions.javac.JavacTypeElement
import com.airbnb.paris.processor.framework.WithJavaSkyProcessor
import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException

/**
 * It's important that base styleables be extracted before new ones are written for the current module, otherwise the latter will be included in the
 * results
 */
internal class BaseStyleableInfoExtractor(override val processor: ParisProcessor) : WithJavaSkyProcessor {

    fun fromEnvironment(): List<BaseStyleableInfo> {
        val baseStyleablesInfo = mutableListOf<BaseStyleableInfo>()
        // TODO: 2/21/21 How to get package with ksp???
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
                                BaseStyleableInfoExtractor(processor).fromElement(
                                    (processingEnv as JavacProcessingEnv).wrapTypeElement(typeElement)
                                )
                            }
                    )
                }
        }
        return baseStyleablesInfo
    }

    fun fromElement(element: XTypeElement): BaseStyleableInfo {
        val elementPackageName = element.packageName
        val elementName = element.name
        val elementType = element.type

        val viewElementType: XType = if (processor.memoizer.proxyClassTypeErasedX.isAssignableFrom(elementType.rawType)) {
            // Get the parameterized type, which should be the view type
            element.superType?.typeArguments?.getOrNull(1) ?: error("No type for $elementType")
        } else {
            elementType
        }

        val viewElement = viewElementType.typeElement!!
        val viewElementPackageName = viewElement.packageName
        val viewElementName = viewElement.name

        val styleable = element.toAnnotationBox(Styleable::class)?.value!!
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

