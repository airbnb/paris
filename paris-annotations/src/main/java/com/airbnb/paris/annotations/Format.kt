package com.airbnb.paris.annotations

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

enum class Format(val statement: String) {

    DEFAULT(""),

    BOOLEAN("getBoolean(%s, false)"),
    CHARSEQUENCE("getText(%s)"),
    CHARSEQUENCE_ARRAY("getTextArray(%s)"),
    COLOR("getColor(%s, -1)"),
    COLOR_STATE_LIST("getColorStateList(%s)"),
    DIMENSION("getDimension(%s, -1f)"),
    DIMENSION_PIXEL_OFFSET("getDimensionPixelOffset(%s, -1)"),
    DIMENSION_PIXEL_SIZE("getDimensionPixelSize(%s, -1)"),
    DRAWABLE("getDrawable(%s)"),
    FLOAT("getFloat(%s, -1f)"),
    FRACTION("getFraction(%s, 1, 1, -1f)"),
    INT("getInt(%s, -1)"),
    INTEGER("getInteger(%s, -1)"),
    NON_RESOURCE_STRING("getNonResourceString(%s)"),
    RESOURCE_ID("getResourceId(%s, -1)"),
    STRING("getString(%s)");

    companion object {

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
