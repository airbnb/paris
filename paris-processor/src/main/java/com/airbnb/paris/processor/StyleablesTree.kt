package com.airbnb.paris.processor

import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.*
import java.util.*
import javax.lang.model.element.*
import javax.lang.model.util.*

internal class StyleablesTree {

    // This is a map of the View class qualified name to the StyleApplier ClassName
    // eg. "android.view.View" -> "com.airbnb.paris.ViewStyleApplier".className()
    private val viewQualifiedNameToStyleApplierClassName = mutableMapOf<String, ClassName>()

    /**
     * Traverses the class hierarchy of the given View type to find and return the first
     * corresponding style applier
     */
    internal fun findStyleApplier(typeUtils: Types, styleablesInfo: List<BaseStyleableInfo>, viewTypeElement: TypeElement): ClassName {
        var styleApplierClassName = viewQualifiedNameToStyleApplierClassName[viewTypeElement.qualifiedName.toString()]
        if (styleApplierClassName != null) {
            return styleApplierClassName
        }

        val type = viewTypeElement.asType()
        // Check to see if the view type is handled by a styleable class
        val styleableInfo = styleablesInfo.find { typeUtils.isSameType(type, it.viewElementType) }
        if (styleableInfo != null) {
            styleApplierClassName = viewTypeElementToStyleApplierClassName(styleableInfo.elementType.asTypeElement(typeUtils))
        } else {
            styleApplierClassName = findStyleApplier(
                    typeUtils,
                    styleablesInfo,
                    viewTypeElement.superclass.asTypeElement(typeUtils))
        }

        viewQualifiedNameToStyleApplierClassName.put(
                viewTypeElement.qualifiedName.toString(),
                styleApplierClassName)
        return styleApplierClassName
    }

    private fun viewTypeElementToStyleApplierClassName(viewTypeElement: TypeElement): ClassName {
        val viewClassName = ClassName.get(viewTypeElement)
        return ClassName.get(viewClassName.packageName(), String.format(Locale.US, STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT, viewClassName.simpleName()))
    }
}
