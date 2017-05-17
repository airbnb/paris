package com.airbnb.paris.annotations

import javax.lang.model.type.TypeMirror

enum class Format(val statement: String) {

    BOOLEAN("getBoolean(%s, false)"),
    CHARSEQUENCE("getText(%s)"),
    CHARSEQUENCE_ARRAY("getTextArray(%s)"),
    COLOR_STATE_LIST("getColorStateList(%s)"),
    DRAWABLE("getDrawable(%s)"),
    FLOAT("getFloat(%s, -1f)"),
    INT("getInt(%s, -1)"),
    STRING("getString(%s)");

    companion object {

        fun fromType(type: TypeMirror): Format {
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
