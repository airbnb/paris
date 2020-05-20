package com.airbnb.paris.processor

import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.squareup.javapoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement

internal class StyleablesTree(
    override val processor: ParisProcessor,
    private val styleablesInfo: List<BaseStyleableInfo>
) : WithParisProcessor {

    // This is a map of the View class qualified name to the StyleApplier class details
    // eg. "android.view.View" -> "com.airbnb.paris.ViewStyleApplier".className()
    private val viewQualifiedNameToStyleApplierClassName = mutableMapOf<Name, StyleApplierDetails>()

    /**
     * Traverses the class hierarchy of the given View type to find and return the first
     * corresponding style applier
     */
    internal fun findStyleApplier(viewTypeElement: TypeElement): StyleApplierDetails {
        return viewQualifiedNameToStyleApplierClassName.getOrPut(viewTypeElement.qualifiedName) {

            val type = viewTypeElement.asType()
            // Check to see if the view type is handled by a styleable class
            val styleableInfo = styleablesInfo.find { isSameType(type, it.viewElementType) }
            if (styleableInfo != null) {
                StyleApplierDetails(
                    annotatedElement = styleableInfo.annotatedElement,
                    className = styleableInfo.styleApplierClassName
                )
            } else {
                findStyleApplier(viewTypeElement.superclass.asTypeElement())
            }
        }
    }
}

data class StyleApplierDetails(
    val annotatedElement: Element,
    val className: ClassName
)
