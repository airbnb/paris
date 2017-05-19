package com.airbnb.paris.annotations

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

enum class Format(val resourcesMethodStatement: String, val typedArrayMethodStatement: String) {

    DEFAULT("", ""),

    BOOLEAN("getBoolean(\$L)", "getBoolean(\$L, false)"),
    CHARSEQUENCE("getText(\$L)", "getText(\$L)"),
    CHARSEQUENCE_ARRAY("getTextArray(\$L)", "getTextArray(\$L)"),
    COLOR("getColor(\$L)", "getColor(\$L, -1)"),
    COLOR_STATE_LIST("getColorStateList(\$L)", "getColorStateList(\$L)"),
    DIMENSION("getDimension(\$L)", "getDimension(\$L, -1f)"),
    DIMENSION_PIXEL_OFFSET("getDimensionPixelOffset(\$L)", "getDimensionPixelOffset(\$L, -1)"),
    DIMENSION_PIXEL_SIZE("getDimensionPixelSize(\$L)", "getDimensionPixelSize(\$L, -1)"),
    DRAWABLE("getDrawable(\$L)", "getDrawable(\$L)"),
    FLOAT(/* TODO */"", "getFloat(\$L, -1f)"),
    FRACTION("getFraction(\$L, 1, 1)", "getFraction(\$L, 1, 1, -1f)"),
    INT("getInteger(\$L)", "getInt(\$L, -1)"),
    INTEGER("getInteger(\$L)", "getInteger(\$L, -1)"),
    NON_RESOURCE_STRING("getNonResourceString(\$L)", "getNonResourceString(\$L)"),
    RESOURCE_ID("getResourceId(\$L)", "getResourceId(\$L, -1)"),
    STRING("getString(\$L)", "getString(\$L)");

    companion object {

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

        fun forField(elementUtils: Elements, typeUtils: Types, element: Element): Format {
            val type = element.asType()
            val viewType = elementUtils.getTypeElement("android.view.View").asType()
            if (typeUtils.isSubtype(type, viewType)) {
                // If the field is a View then the attribute must be a resource to style it
                return RESOURCE_ID
            }

            return forEitherFieldOrMethod(element)
        }

        fun forMethod(element: Element): Format {
            return forEitherFieldOrMethod((element as ExecutableElement).parameters[0])
        }

        private fun forEitherFieldOrMethod(element: Element): Format {
            if (element.hasAnnotation("Px")) {
                return DIMENSION_PIXEL_SIZE
            }

            if (element.hasAnyAnnotation(RES_ANNOTATIONS)) {
                return RESOURCE_ID
            }

            val type = element.asType()
            val typeString = type.toString()
            return when (typeString) {
                "java.lang.Boolean", "boolean" -> BOOLEAN
                "java.lang.CharSequence" -> CHARSEQUENCE
                "java.lang.CharSequence[]" -> CHARSEQUENCE_ARRAY
                "android.content.res.ColorStateList" -> COLOR_STATE_LIST
                "android.graphics.drawable.Drawable" -> DRAWABLE
                "java.lang.Float", "float" -> FLOAT
                "java.lang.Integer", "int" -> INT
                "java.lang.String" -> STRING
                else -> throw IllegalArgumentException(String.format("Invalid type"))
            }
        }


    }
}
