package com.airbnb.paris.processor

import com.squareup.javapoet.ClassName
import java.util.*
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

internal class StyleableClassesTree(private val typeUtils: Types, private val stylealeClassesInfo: List<StyleableClassInfo>) {

    private val CLASS_NAME_FORMAT = "%sStyleApplier"

    private val viewQualifiedNameToStyleApplierClassName = mutableMapOf(
            Pair("android.view.View", ClassName.get("com.airbnb.paris", "ViewStyleApplier")),
            Pair("android.widget.TextView", ClassName.get("com.airbnb.paris", "TextViewStyleApplier")))

    internal fun findFirstStyleableSuperClassName(typeUtils: Types, stylealeClassesInfo: List<StyleableClassInfo>, typeElement: TypeElement): ClassName {
        var styleApplierClassName = viewQualifiedNameToStyleApplierClassName[typeElement.qualifiedName.toString()]
        if (styleApplierClassName != null) {
            return styleApplierClassName
        }

        val type = typeElement.asType()
        if (stylealeClassesInfo.any { typeUtils.isSameType(type, it.type) }) {
            styleApplierClassName = viewTypeElemenetToStyleApplierClassName(typeElement)
        } else {
            styleApplierClassName = findFirstStyleableSuperClassName(
                    typeUtils,
                    stylealeClassesInfo,
                    typeUtils.asElement(typeElement.superclass) as TypeElement)
        }

        viewQualifiedNameToStyleApplierClassName.put(
                typeElement.qualifiedName.toString(),
                styleApplierClassName)
        return styleApplierClassName
    }

    private fun viewTypeElemenetToStyleApplierClassName(viewTypeElement: TypeElement): ClassName {
        val viewClassName = ClassName.get(viewTypeElement)
        return ClassName.get(viewClassName.packageName(), String.format(Locale.US, CLASS_NAME_FORMAT, viewClassName.simpleName()))
    }
}