package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.GeneratedStyleableModule
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.PARIS_MODULES_PACKAGE_NAME
import com.airbnb.paris.processor.PROXY_CLASS_NAME
import com.airbnb.paris.processor.STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT
import com.airbnb.paris.processor.framework.*
import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

internal class BaseStyleableInfoExtractor {

    fun fromEnvironment(): List<BaseStyleableInfo> {
        val baseStyleablesInfo = mutableListOf<BaseStyleableInfo>()
        elements.getPackageElement(PARIS_MODULES_PACKAGE_NAME)?.let { packageElement ->
            packageElement.enclosedElements
                .map { it.getAnnotation(GeneratedStyleableModule::class.java) }
                .forEach { styleableModule ->
                    baseStyleablesInfo.addAll(
                        styleableModule.value
                            .mapNotNull {
                                var typeElement: TypeElement? = null
                                try {
                                    it.value
                                } catch (e: MirroredTypeException) {
                                    typeElement = e.typeMirror.asTypeElement()
                                }
                                typeElement
                            }
                            .map { BaseStyleableInfoExtractor().fromElement(it) }
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
        if (isSubtype(elementType, erasure(PROXY_CLASS_NAME.toTypeMirror()))) {
            // Get the parameterized type, which should be the view type
            viewElementType = (element.superclass as DeclaredType).typeArguments[1]
        } else {
            viewElementType = elementType
        }

        val viewElement = viewElementType.asTypeElement()
        val viewElementPackageName = viewElement.packageName
        val viewElementName = viewElement.simpleName.toString()

        val styleable = element.getAnnotation(Styleable::class.java)
        val styleableResourceName = styleable.value

        return BaseStyleableInfo(
            elementPackageName,
            elementName,
            elementType,
            viewElementPackageName,
            viewElementName,
            viewElementType,
            styleableResourceName
        )
    }
}

internal open class BaseStyleableInfo(
    val elementPackageName: String,
    val elementName: String,
    /**
     * If the styleable class is not a proxy, will be equal to [viewElementType]. Otherwise,
     * will refer to the proxy class
     */
    val elementType: TypeMirror,
    private val viewElementPackageName: String,
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
        baseStyleableInfo.elementPackageName,
        baseStyleableInfo.elementName,
        baseStyleableInfo.elementType,
        baseStyleableInfo.viewElementPackageName,
        baseStyleableInfo.viewElementName,
        baseStyleableInfo.viewElementType,
        baseStyleableInfo.styleableResourceName
    )

    // TODO Make this a property
    fun styleApplierClassName(): ClassName =
        ClassName.get(
            viewElementPackageName,
            String.format(STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT, viewElementName)
        )

    val styleBuilderClassName: ClassName = styleApplierClassName().nestedClass("StyleBuilder")
}

