package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.AndroidClassNames.ATTRIBUTE_SET
import com.airbnb.paris.processor.framework.AndroidClassNames.STYLE_RES
import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.airbnb.paris.processor.models.StyleableInfo
import com.airbnb.paris.processor.utils.lowerCaseFirstLetter
import com.squareup.kotlinpoet.*

/**
 * This generates a kt file with extension functions to style a specific view.
 *
 * For example, for ImageView this will generate a "ImageViewStyleExtensions.kt" file, with several
 * extensions on ImageView to apply a style.
 *
 * These extensions achieve the same effect as using the Paris class.
 */
internal class KotlinStyleExtensionsFile(
    packageName: String,
    private val styleables: List<BaseStyleableInfo>
) : SkyKotlinFile(packageName, "ParisExtensions", {

    styleables.forEach { styleable ->

        fun FunSpec.Builder.addStyleBuilderAsParam() {
            val builderType = styleable.styleBuilderClassName.toKPoet()
            addParameter(
                BUILDER_PARAM_NAME,
                LambdaTypeName.get(receiver = builderType, returnType = UNIT)
            )
        }

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
            addParameter("attrs", ATTRIBUTE_SET)
            addStatement("%T(this).apply(attrs)", styleable.styleApplierClassName().toKPoet())
        }


        if (styleable is StyleableInfo) {
            /**
             * An extension that applies a linked style.
             *
             * Eg: "view.applyGreenStyle()"
             */
            styleable.styles.forEach {
                val styleName = it.formattedName

                function("apply${styleName}Style") {
                    receiver(styleable.viewElementType)
                    addStatement(
                        "%T(this).apply$styleName()",
                        styleable.styleApplierClassName().toKPoet()
                    )
                }
            }
        }

        /**
         * An extension for styling a view via a style builder.
         *
         * Usage is like: "view.style {  // builder as a receiver here  }"
         */
        function("style${styleable.viewElementName}") {
            addModifiers(KModifier.INLINE)

            val paramName = styleable.viewElementName.lowerCaseFirstLetter()
            addParameter(paramName, styleable.viewElementType.asTypeName())

            addStyleBuilderAsParam()

            addStatement(
                "%T().apply($BUILDER_PARAM_NAME).applyTo(%L)",
                styleable.styleBuilderClassName.toKPoet(),
                paramName
            )
        }

        /**
         * A helper (not an extension) for creating a Style object for the given view.
         *
         * Usage is like: "val style = imageViewStyle {  // builder as a receiver here  }"
         */
        function("style${styleable.viewElementName}") {
            addModifiers(KModifier.INLINE)
            returns(STYLE_CLASS_NAME.toKPoet())

            addStyleBuilderAsParam()

            addStatement(
                "return %T().apply($BUILDER_PARAM_NAME).build()",
                styleable.styleBuilderClassName.toKPoet()
            )
        }
    }
})

private const val BUILDER_PARAM_NAME = "builder"