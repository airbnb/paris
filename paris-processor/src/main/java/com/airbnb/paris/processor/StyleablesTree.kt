package com.airbnb.paris.processor

import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement

internal class StyleablesTree(
    override val processor: ParisProcessor,
    private val styleablesInfo: List<BaseStyleableInfo>
) : WithParisProcessor {

    // This is a map of the View class qualified name to the StyleApplier ClassName
    // eg. "android.view.View" -> "com.airbnb.paris.ViewStyleApplier".className()
    private val viewQualifiedNameToStyleApplierClassName = mutableMapOf<String, ClassName>()

    /**
     * Traverses the class hierarchy of the given View type to find and return the first
     * corresponding style applier
     */
    internal fun findStyleApplier(viewTypeElement: TypeElement): ClassName {
        var styleApplierClassName = viewQualifiedNameToStyleApplierClassName[viewTypeElement.qualifiedName.toString()]
        if (styleApplierClassName != null) {
            return styleApplierClassName
        }

        val type = viewTypeElement.asType()
        // Check to see if the view type is handled by a styleable class
        val styleableInfo = styleablesInfo.find { isSameType(type, it.viewElementType) }
        if (styleableInfo != null) {
            styleApplierClassName = styleableInfo.styleApplierClassName
        } else {
            styleApplierClassName = findStyleApplier(viewTypeElement.superclass.asTypeElement())
        }

        viewQualifiedNameToStyleApplierClassName.put(
                viewTypeElement.qualifiedName.toString(),
                styleApplierClassName)
        return styleApplierClassName
    }
}
