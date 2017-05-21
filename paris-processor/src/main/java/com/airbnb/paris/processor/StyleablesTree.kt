package com.airbnb.paris.processor

import com.airbnb.paris.processor.utils.asTypeElement
import com.squareup.javapoet.ClassName
import java.util.*
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

internal class StyleablesTree {

    private val viewQualifiedNameToStyleApplierClassName = mutableMapOf(
            Pair("android.view.View", ClassName.get("com.airbnb.paris", "ViewStyleApplier")),
            Pair("android.widget.TextView", ClassName.get("com.airbnb.paris", "TextViewStyleApplier")))

    /**
     * Traverses the class hierarchy of the given View type to find and return the first
     * corresponding style applier
     */
    internal fun findStyleApplier(typeUtils: Types, styleablesInfo: List<StyleableInfo>, viewTypeElement: TypeElement): ClassName {
        var styleApplierClassName = viewQualifiedNameToStyleApplierClassName[viewTypeElement.qualifiedName.toString()]
        if (styleApplierClassName != null) {
            return styleApplierClassName
        }

        val type = viewTypeElement.asType()
        if (styleablesInfo.any { typeUtils.isSameType(type, it.elementType) }) {
            styleApplierClassName = viewTypeElementToStyleApplierClassName(viewTypeElement)
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
        return ClassName.get(viewClassName.packageName(), String.format(Locale.US, ParisProcessor.STYLE_APPLIER_CLASS_NAME_FORMAT, viewClassName.simpleName()))
    }
}