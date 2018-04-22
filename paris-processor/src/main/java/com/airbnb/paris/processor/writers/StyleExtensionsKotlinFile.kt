package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.EXTENSIONS_FILE_NAME_FORMAT
import com.airbnb.paris.processor.PARIS_KOTLIN_EXTENSIONS_PACKAGE_NAME
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.AndroidClassNames.ATTRIBUTE_SET
import com.airbnb.paris.processor.framework.AndroidClassNames.STYLE_RES
import com.airbnb.paris.processor.models.StyleableInfo
import com.squareup.kotlinpoet.*

/**
 * This generates a kt file with extension functions to style a specific view.
 *
 * For example, for ImageView this will generate a "ImageViewStyleExtensions.kt" file, with several
 * extensions on ImageView to apply a style.
 *
 * All the generated files must be in the same package so that "style" functions all use the same
 * import. Notably this leads to cleaner auto-complete dialogs.
 *
 * These extensions achieve the same effect as using the Paris class.
 *
 * TODO There could be a naming conflict if two @Styleable classes have the same name (but are in
 *      different packages)
 */
internal class StyleExtensionsKotlinFile(
        styleable: StyleableInfo
) : SkyKotlinFile(PARIS_KOTLIN_EXTENSIONS_PACKAGE_NAME, EXTENSIONS_FILE_NAME_FORMAT.format(styleable.elementName), {

    /**
     * An extension for setting a Style object on the view.
     *
     * Usage is like: "view.style(styleObject)"
     */
    function("style") {
        receiver(styleable.viewElementType)
        addParameter("style", STYLE_CLASS_NAME.toKPoet())
        addStatement("%T(this).apply(style)", styleable.styleApplierClassName().toKPoet())
    }

    /**
     * An extension for setting a style res on the view.
     *
     * Usage is like: "view.style(R.style.mystyle)"
     */
    function("style") {
        receiver(styleable.viewElementType)
        addParameter("styleRes", INT) {
            addAnnotation(STYLE_RES)
        }
        addStatement("%T(this).apply(styleRes)", styleable.styleApplierClassName().toKPoet())
    }

    /**
     * An extension for setting an AttributeSet on the view.
     *
     * Usage is like: "view.style(attrs)"
     */
    function("style") {
        receiver(styleable.viewElementType)
        addParameter("attrs", ATTRIBUTE_SET.toKPoet().asNullable())
        addStatement("%T(this).apply(attrs)", styleable.styleApplierClassName().toKPoet())
    }

    /**
     * An extension that applies a linked style.
     *
     * Eg: "view.styleGreen()"
     */
    styleable.styles.forEach {
        val styleName = it.formattedName

        function("style$styleName") {
            receiver(styleable.viewElementType)
            addStatement(
                    "%T(this).apply$styleName()",
                    styleable.styleApplierClassName().toKPoet()
            )
        }
    }

    val builderParam = ParameterSpec.builder(
            "builder",
            LambdaTypeName.get(
                    receiver = styleable.styleBuilderClassName.toKPoet(),
                    returnType = UNIT
            )
    ).build()

    /**
     * A helper (not an extension) for creating a Style object for the given view.
     *
     * Usage is like: "val style = imageViewStyle {  // builder as a receiver here  }"
     */
    function("${styleable.viewElementName.decapitalize()}Style") {
        addModifiers(KModifier.INLINE)
        returns(STYLE_CLASS_NAME.toKPoet())

        addParameter(builderParam)

        addStatement(
                "return %T().apply(%N).build()",
                styleable.styleBuilderClassName.toKPoet(),
                builderParam
        )
    }
})
