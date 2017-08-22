package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Fraction
import com.airbnb.paris.processor.utils.hasAnnotation
import com.airbnb.paris.processor.utils.hasAnyAnnotation
import com.airbnb.paris.processor.utils.isView
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class Format private constructor(
        private val type: Type,
        private val base: Int = 1,
        private val pbase: Int = 1) {

    companion object {

        val RESOURCE_ID = Format(Type.RESOURCE_ID)
        val STYLE = Format(Type.STYLE)

        private enum class Type {
            BOOLEAN,
            CHARSEQUENCE,
            CHARSEQUENCE_ARRAY,
            COLOR,
            COLOR_STATE_LIST,
            DIMENSION,
            DIMENSION_PIXEL_OFFSET,
            DIMENSION_PIXEL_SIZE,
            DRAWABLE,
            FLOAT,
            FRACTION,
            INT,
            INTEGER,
            LAYOUT_DIMENSION,
            NON_RESOURCE_STRING,
            RESOURCE_ID,
            STRING,
            STYLE
        }

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

        fun forElement(elementUtils: Elements, typeUtils: Types, element: Element): Format {
            return if (element.kind == ElementKind.FIELD) {
                forField(elementUtils, typeUtils, element)
            } else {
                forMethod(element)
            }
        }

        private fun forField(elementUtils: Elements, typeUtils: Types, element: Element): Format {
            val type = element.asType()
            if (typeUtils.isView(elementUtils, type)) {
                // If the field is a View then the attribute must be a style or style resource id
                return Format(Type.STYLE)
            }

            return forEitherFieldOrMethodParameter(element)
        }

        private fun forMethod(element: Element): Format {
            return forEitherFieldOrMethodParameter((element as ExecutableElement).parameters[0])
        }

        private fun forEitherFieldOrMethodParameter(element: Element): Format {
            // TODO  Use qualified name of annotations

            if (element.hasAnnotation("ColorInt")) {
                return Format(Type.COLOR)
            }
            if (element.hasAnnotation("Fraction")) {
                val fraction = element.getAnnotation(Fraction::class.java)
                return Format(Type.FRACTION, fraction.base, fraction.pbase)
            }
            if (element.hasAnnotation("LayoutDimension")) {
                return Format(Type.LAYOUT_DIMENSION)
            }
            if (element.hasAnnotation("Px")) {
                return Format(Type.DIMENSION_PIXEL_SIZE)
            }
            if (element.hasAnyAnnotation(RES_ANNOTATIONS)) {
                return Format.RESOURCE_ID
            }

            val typeString = element.asType().toString()
            val formatType = when (typeString) {
                "java.lang.Boolean", "boolean" -> Type.BOOLEAN
                "java.lang.CharSequence" -> Type.CHARSEQUENCE
                "java.lang.CharSequence[]" -> Type.CHARSEQUENCE_ARRAY
                "android.content.res.ColorStateList" -> Type.COLOR_STATE_LIST
                "android.graphics.drawable.Drawable" -> Type.DRAWABLE
                "java.lang.Float", "float" -> Type.FLOAT
                "java.lang.Integer", "int" -> Type.INT
                "java.lang.String" -> Type.STRING
                else -> throw IllegalArgumentException(String.format("Invalid type"))
            }
            return Format(formatType)
        }
    }

    fun resourcesMethodStatement(): String {
        return when (type) {
            Type.BOOLEAN -> "getBoolean(\$L)"
            Type.CHARSEQUENCE -> "getText(\$L)"
            Type.CHARSEQUENCE_ARRAY -> "getTextArray(\$L)"
            Type.COLOR -> "getColor(\$L)"
            Type.COLOR_STATE_LIST -> "getColorStateList(\$L)"
            Type.DIMENSION -> "getDimension(\$L)"
            Type.DIMENSION_PIXEL_OFFSET -> "getDimensionPixelOffset(\$L)"
            Type.DIMENSION_PIXEL_SIZE -> "getDimensionPixelSize(\$L)"
            Type.DRAWABLE -> "getDrawable(\$L)"
            Type.FLOAT -> TODO()
            Type.FRACTION -> "getFraction(\$L, %d, %d)".format(base, pbase)
            Type.INT -> "getInteger(\$L)"
            Type.INTEGER -> "getInteger(\$L)"
            Type.LAYOUT_DIMENSION -> TODO()
            Type.NON_RESOURCE_STRING -> "getNonResourceString(\$L)"
            // Special case
            Type.RESOURCE_ID -> "The parameter is the resource id, this should never be used"
            Type.STRING -> "getString(\$L)"
            Type.STYLE -> TODO()
        }
    }

    fun typedArrayMethodStatement(): String {
        return when (type) {
            Type.BOOLEAN -> "getBoolean(\$L, false)"
            Type.CHARSEQUENCE -> "getText(\$L)"
            Type.CHARSEQUENCE_ARRAY -> "getTextArray(\$L)"
            Type.COLOR -> "getColor(\$L, -1)"
            Type.COLOR_STATE_LIST -> "getColorStateList(\$L)"
            Type.DIMENSION -> "getDimension(\$L, -1f)"
            Type.DIMENSION_PIXEL_OFFSET -> "getDimensionPixelOffset(\$L, -1)"
            Type.DIMENSION_PIXEL_SIZE -> "getDimensionPixelSize(\$L, -1)"
            Type.DRAWABLE -> "getDrawable(\$L)"
            Type.FLOAT -> "getFloat(\$L, -1f)"
            Type.FRACTION -> "getFraction(\$L, %d, %d, -1f)".format(base, pbase)
            Type.INT -> "getInt(\$L, -1)"
            Type.INTEGER -> "getInteger(\$L, -1)"
            Type.LAYOUT_DIMENSION -> "getLayoutDimension(\$L, -1)"
            Type.NON_RESOURCE_STRING -> "getNonResourceString(\$L)"
            Type.RESOURCE_ID -> "getResourceId(\$L, -1)"
            Type.STRING -> "getString(\$L)"
            Type.STYLE -> "getStyle(\$L)"
        }
    }
}
