package com.airbnb.paris.processor

import com.airbnb.paris.processor.utils.asTypeElement
import com.airbnb.paris.processor.utils.className
import com.squareup.javapoet.ClassName
import java.util.*
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

internal class StyleablesTree {

    // This is a map of the View class qualified name to the StyleApplier ClassName
    // eg. "android.view.View" -> "com.airbnb.paris.ViewStyleApplier".className()
    private val viewQualifiedNameToStyleApplierClassName = lazy {
        ParisProcessor.BUILT_IN_STYLE_APPLIERS.entries
                .map { Pair(it.value, it.key.className()) }
                .toMap().toMutableMap()
    }

    /**
     * Traverses the class hierarchy of the given View type to find and return the first
     * corresponding style applier
     */
    internal fun findStyleApplier(typeUtils: Types, styleablesInfo: List<StyleableInfo>, viewTypeElement: TypeElement): ClassName {
        var styleApplierClassName = viewQualifiedNameToStyleApplierClassName.value[viewTypeElement.qualifiedName.toString()]
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

        viewQualifiedNameToStyleApplierClassName.value.put(
                viewTypeElement.qualifiedName.toString(),
                styleApplierClassName)
        return styleApplierClassName
    }

    private fun viewTypeElementToStyleApplierClassName(viewTypeElement: TypeElement): ClassName {
        val viewClassName = ClassName.get(viewTypeElement)
        return ClassName.get(viewClassName.packageName(), String.format(Locale.US, ParisProcessor.STYLE_APPLIER_CLASS_NAME_FORMAT, viewClassName.simpleName()))
    }
}
