package com.airbnb.paris.processor

import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XVariableElement
import androidx.room.compiler.processing.isArray
import androidx.room.compiler.processing.isInt
import androidx.room.compiler.processing.isMethod
import com.airbnb.paris.annotations.Fraction
import com.airbnb.paris.annotations.LayoutDimension
import com.airbnb.paris.processor.framework.AndroidClassNames
import com.airbnb.paris.processor.framework.Memoizer
import com.airbnb.paris.processor.utils.hasAnyAnnotationBySimpleName
import com.airbnb.paris.processor.utils.isBoolean
import com.airbnb.paris.processor.utils.isFieldElement
import com.airbnb.paris.processor.utils.isFloat
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock

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

        fun forElement(memoizer: Memoizer, element: XElement): Format {
            return when {
                element.isFieldElement() -> {
                    forField(memoizer, element)
                }
                element.isMethod() -> {
                    forMethod(element)
                }
                else -> {
                    error("unsupported $element")
                }
            }
        }

        private fun forField(memoizer: Memoizer, element: XFieldElement): Format {
            if (memoizer.androidViewClassTypeX.rawType.isAssignableFrom(element.type)) {
                // If the field is a View then the attribute must be a style or style resource id
                return Format(Type.STYLE)
            }

            return forEitherFieldOrMethodParameter(element)
        }

        private fun forMethod(element: XMethodElement): Format {
            val param = element.parameters.firstOrNull() ?: error("No parameter for $element")
            return forEitherFieldOrMethodParameter(param)
        }

        private fun forEitherFieldOrMethodParameter(element: XVariableElement): Format {
            // TODO Use qualified name of annotations
            // TODO Check that the type of the parameters corresponds to the annotation

            if (element.hasAnnotation(ColorInt::class)) {
                return Format(Type.COLOR)
            }
            element.getAnnotation(Fraction::class)?.value?.let { fraction ->
                return Format(Type.FRACTION, fraction.base, fraction.pbase)
            }
            if (element.hasAnnotation(LayoutDimension::class)) {
                return Format(Type.LAYOUT_DIMENSION)
            }
            // TODO What about Sp?
            if (element.hasAnnotation(Px::class)) {
                return Format(Type.DIMENSION_PIXEL_SIZE)
            }
            if (element.hasAnyAnnotationBySimpleName(RES_ANNOTATIONS)) {
                return Format.RESOURCE_ID
            }

            val type = element.type.makeNonNullable()
            val typeString by lazy { type.typeName.toString() }
            val formatType = when {
                type.isBoolean() -> Type.BOOLEAN
                type.isFloat() -> Type.FLOAT
                type.isInt() -> Type.INT
                type.isTypeOf(CharSequence::class) -> Type.CHARSEQUENCE
                type.isTypeOf(String::class) -> Type.STRING
                typeString == "android.content.res.ColorStateList" -> Type.COLOR_STATE_LIST
                typeString == "android.graphics.Typeface" -> Type.FONT
                typeString == "android.graphics.drawable.Drawable" -> Type.DRAWABLE
                type.isArray() && type.componentType.isTypeOf(CharSequence::class) -> Type.CHARSEQUENCE_ARRAY
                else -> error("Invalid type: $type $typeString")
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

    val isNullable: Boolean
        get() {
            return valueAnnotation == AndroidClassNames.NULLABLE
        }

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
