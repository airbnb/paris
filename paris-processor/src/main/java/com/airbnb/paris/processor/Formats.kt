package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Format
import com.airbnb.paris.processor.utils.hasAnnotation
import com.airbnb.paris.processor.utils.hasAnyAnnotation
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal object Formats {

    private val RES_ANNOTATIONS = hashSetOf(
            "AnimatorRes",
            "AnimRes",
            "AnyRes",
            "ArrayRes",
            "AttrRes",
            "BoolRes",
            "ColorRes",
            "DimenRes",
            "DrawableRes",
            "FontRes",
            "FractionRes",
            "IdRes",
            "IntegerRes",
            "InterpolatorRes",
            "LayoutRes",
            "PluralsRes",
            "RawRes",
            "StringRes",
            "StyleableRes",
            "StyleRes",
            "TransitionRes",
            "XmlRes")

    fun resourcesMethodStatement(format: Format): String {
        return when (format) {
            Format.DEFAULT -> ""
            Format.BOOLEAN -> "getBoolean(\$L)"
            Format.CHARSEQUENCE -> "getText(\$L)"
            Format.CHARSEQUENCE_ARRAY -> "getTextArray(\$L)"
            Format.COLOR -> "getColor(\$L)"
            Format.COLOR_STATE_LIST -> "getColorStateList(\$L)"
            Format.DIMENSION -> "getDimension(\$L)"
            Format.DIMENSION_PIXEL_OFFSET -> "getDimensionPixelOffset(\$L)"
            Format.DIMENSION_PIXEL_SIZE -> "getDimensionPixelSize(\$L)"
            Format.DRAWABLE -> "getDrawable(\$L)"
            Format.FLOAT -> TODO()
            Format.FRACTION -> "getFraction(\$L, 1, 1)"
            Format.INT -> "getInteger(\$L)"
            Format.INTEGER -> "getInteger(\$L)"
            Format.NON_RESOURCE_STRING -> "getNonResourceString(\$L)"
            Format.RESOURCE_ID -> "getResourceId(\$L)"
            Format.STRING -> "getString(\$L)"
        }
    }

    fun typedArrayMethodStatement(format: Format): String {
        return when (format) {
            Format.DEFAULT -> ""
            Format.BOOLEAN -> "getBoolean(\$L, false)"
            Format.CHARSEQUENCE -> "getText(\$L)"
            Format.CHARSEQUENCE_ARRAY -> "getTextArray(\$L)"
            Format.COLOR -> "getColor(\$L, -1)"
            Format.COLOR_STATE_LIST -> "getColorStateList(\$L)"
            Format.DIMENSION -> "getDimension(\$L, -1f)"
            Format.DIMENSION_PIXEL_OFFSET -> "getDimensionPixelOffset(\$L, -1)"
            Format.DIMENSION_PIXEL_SIZE -> "getDimensionPixelSize(\$L, -1)"
            Format.DRAWABLE -> "getDrawable(\$L)"
            Format.FLOAT -> "getFloat(\$L, -1f)"
            Format.FRACTION -> "getFraction(\$L, 1, 1, -1f)"
            Format.INT -> "getInt(\$L, -1)"
            Format.INTEGER -> "getInteger(\$L, -1)"
            Format.NON_RESOURCE_STRING -> "getNonResourceString(\$L)"
            Format.RESOURCE_ID -> "getResourceId(\$L, -1)"
            Format.STRING -> "getString(\$L)"
        }
    }

    fun forElement(elementUtils: Elements, typeUtils: Types, element: Element): Format {
        return if (element.kind == ElementKind.FIELD) {
            forField(elementUtils, typeUtils, element)
        } else {
            forMethod(element)
        }
    }

    private fun forField(elementUtils: Elements, typeUtils: Types, element: Element): Format {
        val type = element.asType()
        val viewType = elementUtils.getTypeElement("android.view.View").asType()
        if (typeUtils.isSubtype(type, viewType)) {
            // If the field is a View then the attribute must be a resource to style it
            return Format.RESOURCE_ID
        }

        return forEitherFieldOrMethodParameter(element)
    }

    private fun forMethod(element: Element): Format {
        return forEitherFieldOrMethodParameter((element as ExecutableElement).parameters[0])
    }

    private fun forEitherFieldOrMethodParameter(element: Element): Format {
        if (element.hasAnnotation("ColorInt")) {
            return Format.COLOR
        }

        if (element.hasAnnotation("Px")) {
            return Format.DIMENSION_PIXEL_SIZE
        }

        if (element.hasAnyAnnotation(RES_ANNOTATIONS)) {
            return Format.RESOURCE_ID
        }

        val type = element.asType()
        val typeString = type.toString()
        return when (typeString) {
            "java.lang.Boolean", "boolean" -> Format.BOOLEAN
            "java.lang.CharSequence" -> Format.CHARSEQUENCE
            "java.lang.CharSequence[]" -> Format.CHARSEQUENCE_ARRAY
            "android.content.res.ColorStateList" -> Format.COLOR_STATE_LIST
            "android.graphics.drawable.Drawable" -> Format.DRAWABLE
            "java.lang.Float", "float" -> Format.FLOAT
            "java.lang.Integer", "int" -> Format.INT
            "java.lang.String" -> Format.STRING
            else -> throw IllegalArgumentException(String.format("Invalid type"))
        }
    }
}
