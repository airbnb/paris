package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Fraction
import com.airbnb.paris.processor.framework.AndroidClassNames
import com.airbnb.paris.processor.framework.AndroidClassNames.RESOURCES_COMPAT
import com.airbnb.paris.processor.framework.hasAnnotation
import com.airbnb.paris.processor.framework.hasAnyAnnotation
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

internal class Format private constructor(
    private val type: Type,
    private val base: Int = 1,
    private val pbase: Int = 1
) {

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
            FONT,
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
            "MenuRes",
            "PluralsRes",
            "RawRes",
            "StringRes",
            "StyleableRes",
            "StyleRes",
            "TransitionRes",
            "XmlRes"
        )

        fun forElement(processor: ParisProcessor, element: Element): Format {
            return if (element.kind == ElementKind.FIELD) {
                forField(processor, element)
            } else {
                forMethod(element)
            }
        }

        private fun forField(processor: ParisProcessor, element: Element): Format {
            val type = element.asType()
            if (processor.isView(type)) {
                // If the field is a View then the attribute must be a style or style resource id
                return Format(Type.STYLE)
            }

            return forEitherFieldOrMethodParameter(element)
        }

        private fun forMethod(element: Element): Format {
            return forEitherFieldOrMethodParameter((element as ExecutableElement).parameters[0])
        }

        private fun forEitherFieldOrMethodParameter(element: Element): Format {
            // TODO Use qualified name of annotations
            // TODO Check that the type of the parameters corresponds to the annotation

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
            // TODO What about Sp?
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
                "android.graphics.Typeface" -> Type.FONT
                "android.graphics.drawable.Drawable" -> Type.DRAWABLE
                "java.lang.Float", "float" -> Type.FLOAT
                "java.lang.Integer", "int" -> Type.INT
                "java.lang.String" -> Type.STRING
                else -> throw IllegalArgumentException(String.format("Invalid type"))
            }
            return Format(formatType)
        }
    }

    val isDimensionType = type in listOf(
        Type.LAYOUT_DIMENSION,
        Type.DIMENSION,
        Type.DIMENSION_PIXEL_OFFSET,
        Type.DIMENSION_PIXEL_SIZE
    )

    val isColorStateListType = type == Type.COLOR_STATE_LIST

    val valueAnnotation: ClassName?
        get() = when (type) {
            Type.COLOR -> AndroidClassNames.COLOR_INT
            Type.CHARSEQUENCE,
            Type.CHARSEQUENCE_ARRAY,
            Type.COLOR_STATE_LIST,
            Type.DRAWABLE,
            Type.FONT,
            Type.STRING -> AndroidClassNames.NULLABLE
            Type.DIMENSION,
            Type.DIMENSION_PIXEL_OFFSET,
            Type.DIMENSION_PIXEL_SIZE -> {
                AndroidClassNames.PX
            }
            Type.RESOURCE_ID -> AndroidClassNames.ANY_RES
            else -> null
        }

    val resAnnotation: ClassName
        get() = when (type) {
            Type.BOOLEAN -> AndroidClassNames.BOOL_RES
            Type.CHARSEQUENCE,
            Type.STRING -> {
                AndroidClassNames.STRING_RES
            }
            Type.CHARSEQUENCE_ARRAY -> AndroidClassNames.ARRAY_RES
            Type.COLOR,
            Type.COLOR_STATE_LIST -> {
                AndroidClassNames.COLOR_RES
            }
            Type.DIMENSION,
            Type.DIMENSION_PIXEL_OFFSET,
            Type.DIMENSION_PIXEL_SIZE,
            Type.LAYOUT_DIMENSION -> {
                AndroidClassNames.DIMEN_RES
            }
            Type.DRAWABLE -> AndroidClassNames.DRAWABLE_RES
            Type.FONT -> AndroidClassNames.FONT_RES
            Type.FRACTION -> AndroidClassNames.FRACTION_RES
            Type.INT,
            Type.INTEGER -> {
                AndroidClassNames.INTEGER_RES
            }
            Type.NON_RESOURCE_STRING -> AndroidClassNames.ANY_RES // TODO Not sure about this one
            Type.FLOAT -> AndroidClassNames.ANY_RES // TODO There's no FloatRes so... what?
            Type.STYLE -> AndroidClassNames.STYLE_RES
            Type.RESOURCE_ID -> AndroidClassNames.ANY_RES
        }

    fun resourcesMethodCode(contextVar: String, resourcesVar: String, valueResIdCode: CodeBlock): CodeBlock {
        val statement = when (type) {
            Type.BOOLEAN -> "getBoolean(\$L)"
            Type.CHARSEQUENCE -> "getText(\$L)"
            Type.CHARSEQUENCE_ARRAY -> "getTextArray(\$L)"
            Type.COLOR -> "getColor(\$L)"
            Type.COLOR_STATE_LIST -> "getColorStateList(\$L)"
            Type.DIMENSION -> "getDimension(\$L)"
            Type.DIMENSION_PIXEL_OFFSET -> "getDimensionPixelOffset(\$L)"
            Type.DIMENSION_PIXEL_SIZE -> "getDimensionPixelSize(\$L)"
            Type.DRAWABLE -> "getDrawable(\$L)"
            Type.FRACTION -> "getFraction(\$L, %d, %d)".format(base, pbase)
            Type.INT -> "getInteger(\$L)"
            Type.INTEGER -> "getInteger(\$L)"
            Type.NON_RESOURCE_STRING -> "getNonResourceString(\$L)"
            Type.STRING -> "getString(\$L)"

        // Using extension functions because unsupported by Resources
            Type.LAYOUT_DIMENSION -> "\$T.getLayoutDimension(\$L, \$L)"
            Type.FLOAT -> "\$T.getFloat(\$L, \$L)"
            Type.STYLE -> "\$T.getStyle(\$L, \$L)"

        // Using ResourcesCompat with context and font resource arguments
            Type.FONT -> "\$T.getFont(\$L, \$L)"

        // Special case, the resource id is the value
            Type.RESOURCE_ID -> "\$L"
        }

        return when (type) {
            Type.BOOLEAN, Type.CHARSEQUENCE, Type.CHARSEQUENCE_ARRAY, Type.COLOR,
            Type.COLOR_STATE_LIST, Type.DIMENSION, Type.DIMENSION_PIXEL_OFFSET,
            Type.DIMENSION_PIXEL_SIZE, Type.DRAWABLE, Type.FRACTION, Type.INT, Type.INTEGER,
            Type.NON_RESOURCE_STRING, Type.STRING -> {
                CodeBlock.of("\$L.$statement", resourcesVar, valueResIdCode)
            }
            Type.FLOAT, Type.LAYOUT_DIMENSION, Type.STYLE -> {
                CodeBlock.of(
                    statement,
                    RESOURCES_EXTENSIONS_CLASS_NAME,
                    resourcesVar,
                    valueResIdCode
                )
            }
            Type.FONT -> {
                CodeBlock.of(
                    statement,
                    CONTEXT_EXTENSIONS_CLASS_NAME,
                    contextVar,
                    valueResIdCode
                )
            }
            Type.RESOURCE_ID -> {
                CodeBlock.of(statement, valueResIdCode)
            }
        }
    }

    fun typedArrayMethodCode(typedArrayVariable: String, attrResIdCode: CodeBlock): CodeBlock {
        return CodeBlock.of(
            "\$L." + when (type) {
                Type.BOOLEAN -> "getBoolean(\$L)"
                Type.CHARSEQUENCE -> "getText(\$L)"
                Type.CHARSEQUENCE_ARRAY -> "getTextArray(\$L)"
                Type.COLOR -> "getColor(\$L)"
                Type.COLOR_STATE_LIST -> "getColorStateList(\$L)"
                Type.DIMENSION -> "getDimension(\$L)"
                Type.DIMENSION_PIXEL_OFFSET -> "getDimensionPixelOffset(\$L)"
                Type.DIMENSION_PIXEL_SIZE -> "getDimensionPixelSize(\$L)"
                Type.DRAWABLE -> "getDrawable(\$L)"
                Type.FLOAT -> "getFloat(\$L)"
                Type.FRACTION -> "getFraction(\$L, %d, %d)".format(base, pbase)
                Type.FONT -> "getFont(\$L)"
                Type.INT -> "getInt(\$L)"
                Type.INTEGER -> "getInteger(\$L)"
                Type.LAYOUT_DIMENSION -> "getLayoutDimension(\$L)"
                Type.NON_RESOURCE_STRING -> "getNonResourceString(\$L)"
                Type.RESOURCE_ID -> "getResourceId(\$L)"
                Type.STRING -> "getString(\$L)"
                Type.STYLE -> "getStyle(\$L)"
            }, typedArrayVariable, attrResIdCode
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Format

        if (type != other.type) return false
        if (base != other.base) return false
        if (pbase != other.pbase) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + base
        result = 31 * result + pbase
        return result
    }
}
