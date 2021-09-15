package com.airbnb.paris.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.squareup.javapoet.ClassName

internal class StyleablesTree(
    val processor: ParisProcessor,
    private val styleablesInfo: List<BaseStyleableInfo>
) {

    // This is a map of the View class qualified name to the StyleApplier class details
    // eg. "android.view.View" -> "com.airbnb.paris.ViewStyleApplier".className()
    private val viewQualifiedNameToStyleApplierClassName = mutableMapOf<XTypeElement, StyleApplierDetails?>()

    /**
     * Traverses the class hierarchy of the given View type to find and return the first
     * corresponding style applier
     */
    internal fun findStyleApplier(viewTypeElement: XTypeElement, errorContext: (() -> String)? = null): StyleApplierDetails {
        return findStyleApplierRecursive(viewTypeElement)
            ?: error("Could not find style applier for ${viewTypeElement.qualifiedName} ${viewTypeElement.type}. " +
                    errorContext?.invoke()?.let { "$it. " }.orEmpty() +
                    "Available types are ${styleablesInfo.map { it.viewElementType }}")
    }

    private fun findStyleApplierRecursive(viewTypeElement: XTypeElement): StyleApplierDetails? {
        return viewQualifiedNameToStyleApplierClassName.getOrPut(viewTypeElement) {

            val type = viewTypeElement.type
            // Check to see if the view type is handled by a styleable class
            val styleableInfo = styleablesInfo.find { type.isSameType(it.viewElementType) }
            if (styleableInfo != null) {
                StyleApplierDetails(
                    annotatedElement = styleableInfo.annotatedElement,
                    className = styleableInfo.styleApplierClassName
                )
            } else {
                val superType = viewTypeElement.superType?.typeElement ?: return@getOrPut null
                findStyleApplier(superType)
            }
        }
    }
}

data class StyleApplierDetails(
    val annotatedElement: XElement,
    val className: ClassName
)
