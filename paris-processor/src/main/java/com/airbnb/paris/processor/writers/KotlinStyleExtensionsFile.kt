package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.framework.SkyKotlinFile
import com.airbnb.paris.processor.framework.function
import com.airbnb.paris.processor.framework.receiver
import com.airbnb.paris.processor.framework.toKPoet
import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.airbnb.paris.processor.models.StyleableInfo
import com.airbnb.paris.processor.utils.lowerCaseFirstLetter
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.UNIT
import jdk.nashorn.internal.objects.NativeFunction.function

/**
 * This generates a kt file with extension functions to style a specific view.
 *
 * For example, for ImageView this will generate a "ImageViewStyleExtensions.kt" file, with several
 * extensions on ImageView to apply a style.
 *
 * These extensions achieve the same effect as using the Paris class.
 */
internal class KotlinStyleExtensionsFile(
    val styleable: StyleableInfo
) : SkyKotlinFile(styleable.elementPackageName, "${styleable.viewElementName}StyleExtensions", {

    fun FunSpec.Builder.addStyleBuilderAsParam() {
        val builderType = styleable.styleBuilderClassName.toKPoet()
        addParameter(
            BUILDER_PARAM_NAME,
            LambdaTypeName.get(receiver = builderType, returnType = UNIT)
        )
    }

//
//    // Linked style
//    fun View.styleGreen() {
//        ViewStyleApplier(this).apply(R.style.Green)
//    }

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
     * An extension for styling a view via a style builder.
     *
     * Usage is like: "view.style {  // builder as a receiver here  }"
     */
    function("style") {
        receiver(styleable.viewElementType)
        addStyleBuilderAsParam()
        addStatement(
            "%T().apply($BUILDER_PARAM_NAME).applyTo(this)",
            styleable.styleBuilderClassName.toKPoet()
        )
    }

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

    /**
     * A helper (not an extension) for creating a Style object for the given view.
     *
     * Usage is like: "val style = imageViewStyle {  // builder as a receiver here  }"
     */
    function("${styleable.viewElementName.lowerCaseFirstLetter()}Style") {
        addModifiers(KModifier.INLINE)
        returns(STYLE_CLASS_NAME.toKPoet())

        addStyleBuilderAsParam()

        addStatement(
            "return %T().apply($BUILDER_PARAM_NAME).build()",
            styleable.styleBuilderClassName.toKPoet()
        )
    }


})

private const val BUILDER_PARAM_NAME = "builder"